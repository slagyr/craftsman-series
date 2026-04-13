
import junit.framework.*;

import java.io.*;
import java.net.Socket;

import com.objectmentor.SocketService.SocketServer;
import com.objectmentor.SocketService.SocketService;

class TestSMCRServer implements SocketServer {
  public String filename = "noFileName";
  public boolean fileReceived = false;
  private ObjectOutputStream os;
  private ObjectInputStream is;
  public String command;

  public void serve(Socket socket) {
    try {
      os = new ObjectOutputStream(socket.getOutputStream());
      is = new ObjectInputStream(socket.getInputStream());
      os.writeObject("SMCR Test Server");
      os.flush();
      parse(is.readObject());
    } catch (Exception e) {
    }
  }

  private void parse(Object cmd) throws Exception {
    if (cmd != null) {
      if (cmd instanceof CompileFileTransaction) {
        CompileFileTransaction cft = (CompileFileTransaction) cmd;
        filename = cft.getFilename();
        fileReceived = true;
        TestSMCRemoteClient.createTestFile("resultFile.java", "Some content.");
        CompilerResultsTransaction crt =
          new CompilerResultsTransaction(null, "resultFile.java");
        os.writeObject(crt);
        os.flush();
      }
    }
  }
}

public class TestSMCRemoteClient extends TestCase {
  private SMCRemoteClient c;
  private static final int SMCPORT = 9000;
  private TestSMCRServer server;
  private SocketService smc;

  public TestSMCRemoteClient(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    c = new SMCRemoteClient();
    server = new TestSMCRServer();
    smc = new SocketService(SMCPORT, server);
  }

  protected void tearDown() throws Exception {
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

  public void testFileDoesNotExist() throws Exception {
    c.setFilename("thisFileDoesNotExist");
    boolean prepared = c.prepareFile();
    assertEquals(false, prepared);
  }

  public static File createTestFile(String name, String content) throws IOException {
    File f = new File(name);
    FileOutputStream stream = new FileOutputStream(f);
    stream.write(content.getBytes());
    stream.close();
    return f;
  }

  public void testConnectToSMCRemoteServer() throws Exception {
    boolean connection = c.connect();
    assertTrue(connection);
  }

  public void testCompileFile() throws Exception {
    File f = createTestFile("testSendFile", "I am sending this file.");
    c.setFilename("testSendFile");
    assertTrue(c.connect());
    assertTrue(c.prepareFile());
    assertTrue(c.compileFile());
    Thread.sleep(50);
    assertTrue(server.fileReceived);
    assertEquals("testSendFile", server.filename);
    f.delete();
    File resultFile = new File("resultFile.java");
    assertTrue("Result file does not exist", resultFile.exists());
    resultFile.delete();
  }

  public void testMain() throws Exception {
    File f = createTestFile("myFile.sm", "the content");
    SMCRemoteClient.main(new String[]{"myFile.sm"});
    f.delete();
    File resultFile = new File("resultFile.java");
    assertTrue(resultFile.exists());
    resultFile.delete();
  }
}