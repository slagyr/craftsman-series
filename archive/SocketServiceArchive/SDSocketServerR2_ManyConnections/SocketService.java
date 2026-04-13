import java.io.IOException;
import java.net.*;

public class SocketService {
  private ServerSocket serverSocket = null;
  private int connections = 0;
  private Thread serverThread = null;
  private boolean running = false;
  private SocketServer itsServer;

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
              itsServer.serve(s);
              s.close();
              connections++;
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
    serverSocket.close();
  }

  public int connections() {
    return connections;
  }
}
