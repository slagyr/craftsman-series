import java.io.*;
import java.net.*;

public class SocketService {
  private ServerSocket serverSocket = null;
  private Thread serverThread = null;
  private boolean running = false;
  private SocketServer itsService = null;

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
              itsService.serve(s);
              s.close();
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
}
