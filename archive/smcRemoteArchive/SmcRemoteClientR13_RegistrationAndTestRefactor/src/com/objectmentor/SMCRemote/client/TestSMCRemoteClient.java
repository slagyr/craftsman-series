package com.objectmentor.SMCRemote.client;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import com.objectmentor.SMCRemote.transactions.*;
import com.objectmentor.SocketService.*;

import java.io.*;
import java.net.Socket;

class MockSMCRServer implements SocketServer {
  public String filename = "noFileName";
  public boolean fileReceived = false;
  private ObjectOutputStream os;
  private ObjectInputStream is;
  public String generator;
  public boolean isLoggedIn = false;

  public void serve(Socket socket) {
    try {
      os = new ObjectOutputStream(socket.getOutputStream());
      is = new ObjectInputStream(socket.getInputStream());
      os.writeObject("SMCR Test Server");
      os.flush();
      while (true) {
        SocketTransaction t = (SocketTransaction) is.readObject();
        t.accept(processor);
      }
    } catch (Exception e) {
    }
  }

  private SocketTransactionProcessor processor = new SocketTransactionProcessor() {
    public void process(CompileFileTransaction t) {
      try {
        File f1 = TestSMCRemoteClient.createTestFile("myFile.java", "wow");
        File f2 = TestSMCRemoteClient.createTestFile("file2.java", "ick");
        filename = t.getFilename();
        generator = t.getGenerator();

        CompilerResultsTransaction crt = new CompilerResultsTransaction();
        crt.loadFiles(null, new String[]{"myFile.java", "file2.java"});

        f1.delete();
        f2.delete();
        crt.getStdoutLines().add("compile diagnostics");
        crt.getStderrLines().add("stderr message");
        os.writeObject(crt);
        os.flush();

        fileReceived = true;
      } catch (IOException e) {
      }
    }

    public void process(LoginTransaction t) throws Exception {
      LoginResponseTransaction lrt = new LoginResponseTransaction(true);
      os.writeObject(lrt);
      os.flush();
      isLoggedIn = true;
    }
  };
}

public class TestSMCRemoteClient extends TestCase {

  private static final int SMCPORT = 9000;

  public static void main(String[] args) {
    TestRunner.main(new String[]{"com.objectmentor.SMCRemote.client.TestSMCRemoteClient"});
  }

  public TestSMCRemoteClient(String name) {
    super(name);
  }

  private SMCRemoteClient c;
  private MockSMCRServer server;
  private SocketService smc;

  public void setUp() throws Exception {
    c = new SMCRemoteClient();
    server = new MockSMCRServer();
    smc = new SocketService(SMCPORT, server);
  }

  public void tearDown() throws Exception {
    c.close();
    smc.close();
  }

  public void testParseCommandLine() throws Exception {
    c.parseCommandLine(new String[]{"filename"});
    assertEquals("filename", c.getFilename());
    assertEquals(SMCRemoteClient.DEFAULT_HOST, c.getHost());
    assertEquals(Integer.parseInt(SMCRemoteClient.DEFAULT_PORT), c.getPort());
    assertEquals(SMCRemoteClient.DEFAULT_GENERATOR, c.getGenerator());
    assert(!c.isVerbose());

    assert(c.parseCommandLine(new String[] {"-p","999","-h","objectmentor.com", "-v", "-g", "C++", "f.sm"}));
    assertEquals("f.sm", c.getFilename());
    assertEquals("bad host", "objectmentor.com",c.getHost());
    assertEquals("bad port", 999, c.getPort());
    assertEquals("bad generator", "C++",c.getGenerator());
    assert("verbose",c.isVerbose());
  }

  public void testParseInvalidCommandLine() {
    assert("no arguments", !c.parseCommandLine(new String[0]));
    assert("no filename",  !c.parseCommandLine(new String[] {"-h","dodah.com"}));
    assert("too many files",!c.parseCommandLine(new String[] {"file1","file2"}));
    assert("Bad Argument",!c.parseCommandLine(new String[] {"-x","file1"}));
    assert("Bad Port", !c.parseCommandLine(new String[] {"-p","bad port"}));
  }

  static File createTestFile(String name, String content) throws IOException {
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
    assert(connection);
  }

  public void testCompileFile() throws Exception {
    String filename = "testSendFile";
    File f = createTestFile(filename, "I am sending this file.");
    c.setFilename(filename);
    assert(c.connect());
    assert(c.prepareFile());
    assert(c.compileFile() != null);
    Thread.sleep(50);
    assert(server.fileReceived);
    assertEquals(filename, server.filename);
    assertEquals("Bad Generator", "java" ,server.generator);
    f.delete();
  }

  public void testMain() throws Exception {
    ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
    ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
    PrintStream sysout = System.out;
    PrintStream syserr = System.err;

    System.setOut(new PrintStream(stdoutBuffer));
    System.setErr(new PrintStream(stderrBuffer));

    File f = createTestFile("myFile.sm", "the content");
    SMCRemoteClient.main(new String[]{"-g", "C++", "myFile.sm"});
    System.setOut(sysout);
    System.setErr(syserr);

    stdoutBuffer.close();
    stderrBuffer.close();

    Thread.yield();
    f.delete();
    File file1 = new File("myFile.java");
    File file2 = new File("file2.java");
    boolean file1Exists = file1.exists();
    boolean file2Exists = file2.exists();
    boolean exists = file1Exists && file2Exists;

    int file1Len = (int) file1.length();
    int file2Len = (int) file2.length();

    if (file1Exists) file1.delete();
    if (file2Exists) file2.delete();

    assert("Not logged in", server.isLoggedIn);
    assert("not received", server.fileReceived);
    assertEquals("bad generator", "C++", server.generator);
    assert("One or more files doesn't exist", exists);
    assert("f1 zero", file1Len > 0);
    assert("f2 zero", file2Len > 0);

    assert("consoleMessage", stdoutBuffer.toString().startsWith("compile diagnostics"));
    assert("Stderr Message", stderrBuffer.toString().startsWith("stderr message"));
  }
}