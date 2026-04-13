import junit.framework.TestCase;
import junit.swingui.TestRunner;

import java.io.*;
import java.net.Socket;

public class TestSocketServer extends TestCase {
  private int connections = 0;
  private SocketServer connectionCounter;

  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestSocketServer"});
  }

  public TestSocketServer(String name) {
    super(name);
    connectionCounter = new  SocketServer() {
      public void serve(Socket s) {
        connections++;
      }
    };
  }

  public void setUp() throws Exception {
  }

  public void tearDown() throws Exception {
  }

  public void testOneConnection() throws Exception {
    SocketService ss = new SocketService();
    ss.serve(999, connectionCounter);
    connect(999);
    ss.close();
    assertEquals(1, connections);
  }

  public void testManyConnections() throws Exception {
    SocketService ss = new SocketService();
    ss.serve(999, connectionCounter);
    for (int i = 0; i < 10; i++)
      connect(999);
    ss.close();
    assertEquals(10, connections);
  }

//  public void testSendMessage() throws Exception
//  {
//    SocketService ss = new SocketService();
//    ss.serve(999, new HelloService());
//    Socket s = new Socket("localhost", 999);
//    InputStream is = s.getInputStream();
//    InputStreamReader isr = new InputStreamReader(is);
//    BufferedReader br = new BufferedReader(isr);
//    String answer = br.readLine();
//    s.close();
//    assertEquals("Hello", answer);
//  }

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
