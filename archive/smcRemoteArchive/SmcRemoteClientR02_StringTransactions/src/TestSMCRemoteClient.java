import com.objectmentor.SocketService.SocketServer;
import com.objectmentor.SocketService.SocketService;
import junit.framework.TestCase;
import junit.swingui.TestRunner;

import java.io.*;
import java.net.Socket;

class TestSMCRServer implements SocketServer {
  public String filename = "noFileName";
  public long fileLength = -1;
  public boolean fileReceived = false;
  private PrintStream os;
  private BufferedReader is;
  public char[] content;
  public String command;

  public void serve(Socket socket) {
    try {
      os = new PrintStream(socket.getOutputStream());
      is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      os.println("SMCR Test Server");
      os.flush();
      parse(is.readLine());
    } catch (Exception e) {
    }
  }

  private void parse(String cmd) throws Exception {
    if (cmd != null) {
      if (cmd.equals("Sending")) {
        filename = is.readLine();
        fileLength = Long.parseLong(is.readLine());
        content = new char[(int)fileLength];
        is.read(content,0,(int)fileLength);
        fileReceived = true;
      }
    }
  }
}

public class TestSMCRemoteClient extends TestCase {

  private static final int SMCPORT = 9000;

  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestSMCRemoteClient"});
  }

  public TestSMCRemoteClient(String name) {
    super(name);
  }

  private SMCRemoteClient c;
  private TestSMCRServer server;
  private SocketService smc;

  public void setUp() throws Exception {
    c = new SMCRemoteClient();
    server = new TestSMCRServer();
    smc = new SocketService(SMCPORT, server);
  }

  public void tearDown() throws Exception {
    c.close();
    smc.close();
  }

  public void testParseCommandLine() throws Exception {
    c.parseCommandLine(new String[]{"filename"});
    assertEquals("filename", c.filename());
  }

  public void testParseInvalidCommandLine() {
    boolean result = c.parseCommandLine(new String[0]);
   assertTrue("result should be false", !result);
  }

  public void testCountBytesInFile() throws Exception {
    File f = createTestFile("testFile", "some text");
    c.setFilename("testFile");
    boolean prepared = c.prepareFile();
    f.delete();
    assertTrue(prepared);
    assertEquals(9, c.getFileLength());
  }

  private File createTestFile(String name, String content) throws IOException {
    File f = new File(name);
    FileOutputStream stream = new FileOutputStream(f);
    stream.write(content.getBytes());
    stream.close();
    return f;
  }

  public void testFileDoesNotExist() throws Exception {
    c.setFilename("thisFileDoesNotExist");
    boolean prepared = c.prepareFile();
    assertEquals(false, prepared);
  }

  public void testConnectToSMCRemoteServer() throws Exception {
    boolean connection = c.connect();
    assertTrue(connection);
  }
  //todo
  public void testSendFile() throws Exception {
    File f = createTestFile("testSendFile", "I am sending this file.");
    c.setFilename("testSendFile");
    assertTrue(c.connect());
    assertTrue(c.prepareFile());
    assertTrue(c.sendFile());
    Thread.sleep(50);
    assertTrue(server.fileReceived);
    assertEquals("testSendFile", server.filename);
    assertEquals(23, server.fileLength);
    assertEquals("I am sending this file.", new String(server.content));
    f.delete();
  }
}