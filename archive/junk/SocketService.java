import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class SocketService {
  private ServerSocket serverSocket = null;
  private Thread serverThread = null;
  private boolean running = false;
  private SocketServer itsService = null;
  private LinkedList threads = new LinkedList();

  public void serve(int port, SocketServer service) throws Exception {
    itsService = service;
    serverSocket = new ServerSocket(port);
    serverThread = new Thread(
      new Runnable() {
        public void run() {
          running = true;
          while (running) {
            try {
              Socket s = serverSocket.accept();
              Thread serviceThread = new Thread(new ServiceRunnable(s));
              synchronized (threads) {
                threads.add(serviceThread);
              }
              serviceThread.start();
            } catch (IOException e) {
            }
          }
        }
      }
    );
    serverThread.start();
  }

  public void close() throws Exception {
    running = false;
//    serverThread.interrupt();
    serverSocket.close();
    serverThread.join();
    while (threads.size() > 0) {
      Thread t;
      synchronized (threads) {
        t = (Thread) threads.getFirst();
        threads.remove(t);
      }
      t.join();
    }
  }

  public static PrintStream GetPrintStream(Socket s) throws IOException {
    OutputStream os = s.getOutputStream();
    PrintStream ps = new PrintStream(os);
    return ps;
  }

  public static BufferedReader GetBufferedReader(Socket s) throws IOException {
    InputStream is = s.getInputStream();
    InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);
    return br;
  }

  class ServiceRunnable implements Runnable {
    private Socket itsSocket;

    ServiceRunnable(Socket s) {
      itsSocket = s;
    }

    public void run() {
      try {
        itsService.serve(itsSocket);
        synchronized (threads) {
          threads.remove(Thread.currentThread());
        }
        itsSocket.close();
      } catch (IOException e) {
      }
    }
  }
}
