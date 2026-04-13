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
  public String[] command;

  public void serve(Socket socket) {
    try {
      os = new ObjectOutputStream(socket.getOutputStream());
      is = new ObjectInputStream(socket.getInputStream());
      os.writeObject("SMCR Test Server");
      os.flush();
      SocketTransaction t = (SocketTransaction) is.readObject();
      t.accept(processor);
    } catch (Exception e) {
    }
  }

  private SocketTransactionProcessor processor = new SocketTransactionProcessor() {
    public void process(CompilerResultsTransaction t) {
    }

    public void process(CompileFileTransaction t) {
      try {
        File f1 = TestSMCRemoteClient.createTestFile("myFile.java", "wow");
        File f2 = TestSMCRemoteClient.createTestFile("file2.java", "ick");
        filename = t.getFileName();
        command = t.getArgs();

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
    assertEquals("filename", c.filename());
  }

  public void testParseInvalidCommandLine() {
    boolean result = c.parseCommandLine(new String[0]);
    assert("result should be false", !result);
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
    String args[] = new String[]{filename, "hay", "dere", "Eh?"};
    File f = createTestFile(filename, "I am sending this file.");
    c.setFilename(filename);
    assert(c.connect());
    assert(c.prepareFile());
    assert(c.compileFile(args) != null);
    Thread.sleep(50);
    assert(server.fileReceived);
    assertEquals(filename, server.filename);
    f.delete();
    assertEquals("", filename, server.command[0]);
    assertEquals("", "hay", server.command[1]);
    assertEquals("", "dere", server.command[2]);
    assertEquals("", "Eh?", server.command[3]);
  }

  public void testMain() throws Exception {
    ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
    ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
    PrintStream sysout = System.out;
    PrintStream syserr = System.err;

    System.setOut(new PrintStream(stdoutBuffer));
    System.setErr(new PrintStream(stderrBuffer));

    File f = createTestFile("myFile.sm", "the content");
    SMCRemoteClient.main(new String[]{"myFile.sm"});
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

    assert("not received", server.fileReceived);
    assert("One or more files doesn't exist", exists);
    assert("f1 zero", file1Len > 0);
    assert("f2 zero", file2Len > 0);

    assert("consoleMessage", stdoutBuffer.toString().startsWith("compile diagnostics"));
    assert("Stderr Message", stderrBuffer.toString().startsWith("stderr message"));
  }
}