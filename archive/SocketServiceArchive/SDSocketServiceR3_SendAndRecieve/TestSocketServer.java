import junit.framework.TestCase;
import junit.swingui.TestRunner;

import java.io.*;
import java.net.Socket;

public class TestSocketServer extends TestCase {
  private int connections = 0;
  private SocketServer connectionCounter;
  private SocketService ss;

  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestSocketServer"});
  }

  public TestSocketServer(String name) {
    super(name);
    connectionCounter = new SocketServer() {
      public void serve(Socket s) {
        connections++;
      }
    };
  }

  public void setUp() throws Exception {
    ss = new SocketService();
  }

  public void tearDown() throws Exception {
    ss.close();
  }

  public void testOneConnection() throws Exception {
    ss.serve(999, connectionCounter);
    connect(999);
    assertEquals(1, connections);
  }

  public void testManyConnections() throws Exception {
    ss.serve(999, connectionCounter);
    for (int i = 0; i < 10; i++)
      connect(999);
    assertEquals(10, connections);
  }

  public void testSendMessage() throws Exception {
    ss.serve(999, new HelloServer());
    Socket s = new Socket("localhost", 999);
    BufferedReader br = SocketService.getBufferedReader(s);
    String answer = br.readLine();
    assertEquals("Hello", answer);
  }

  public void testReceiveMessage() throws Exception {
    ss.serve(999, new EchoService());
    Socket s = new Socket("localhost", 999);
    BufferedReader br = SocketService.getBufferedReader(s);
    PrintStream ps = SocketService.getPrintStream(s);
    ps.println("MyMessage");
    String answer = br.readLine();
    s.close();
    assertEquals("MyMessage", answer);
  }

  private void connect(int port) {
    try {
      Socket s = new Socket("localhost", port);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
      }
      s.close();
    } catch (IOException e) {
      fail("could not connect");
    }
  }
}

class HelloServer implements SocketServer {
  public void serve(Socket s) {
    try {
      OutputStream os = s.getOutputStream();
      PrintStream ps = new PrintStream(os);
      ps.println("Hello");
    } catch (IOException e) {
    }
  }
}

class EchoService implements SocketServer {
  public void serve(Socket s) {
    try {
      BufferedReader br = SocketService.getBufferedReader(s);
      PrintStream ps = SocketService.getPrintStream(s);
      String token = br.readLine();
      ps.println(token);
    } catch (IOException e) {
    }
  }
}