import java.io.*;
import java.net.*;
import java.util.*;

public class SocketService {
  private ServerSocket serverSocket = null;
  private Thread serverThread = null;
  private boolean running = false;
  private SocketServer itsServer;
  private List serverThreads = Collections.synchronizedList(new LinkedList());

  public void serve(int port, SocketServer server) throws Exception {
    itsServer = server;
    serverSocket = new ServerSocket(port);
    serverThread = new Thread(
      new Runnable() {
        public void run() {
          running = true;
          while (running) {
            try {
              Socket s = serverSocket.accept();
              Thread serverThread = new Thread(new ServiceRunnable(s));
              serverThreads.add(serverThread);
              serverThread.start();
            } catch (IOException e) {
            }
          }
        }
      }
    );
    serverThread.start();
  }

  public void close() throws Exception {
    if (running) {
      running = false;
      serverSocket.close();
      serverThread.join();
      while (serverThreads.size() > 0) {
        Thread t = (Thread)serverThreads.get(0);
        serverThreads.remove(t);
        t.join();
      }
    } else {
      serverSocket.close();
    }
  }

  public static PrintStream getPrintStream(Socket s) throws IOException {
    OutputStream os = s.getOutputStream();
    PrintStream ps = new PrintStream(os);
    return ps;
  }

  public static BufferedReader getBufferedReader(Socket s) throws IOException {
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
        itsServer.serve(itsSocket);
        serverThreads.remove(Thread.currentThread());
        itsSocket.close();
      } catch (IOException e) {
      }
    }
  }
}
