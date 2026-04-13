package com.objectmentor.SMCRemote.client;

import junit.swingui.TestRunner;

import com.objectmentor.SMCRemote.transactions.*;
import com.objectmentor.SocketService.SocketService;

import java.io.*;

class MockRemoteCompilerServer extends MockServerBase {
  public String filename = "noFileName";
  public boolean fileReceived = false;
  public String generator;
  public boolean isLoggedIn = false;

  class RemoteCompilerTransactionProcessor extends SocketTransactionProcessor {
    public void process(CompileFileTransaction t) throws Exception {
      try {
        File f1 = TestRemoteCompiler.createTestFile("myFile.java", "wow");
        File f2 = TestRemoteCompiler.createTestFile("file2.java", "ick");
        filename = t.getFilename();
        generator = t.getGenerator();

        CompilerResultsTransaction crt = new CompilerResultsTransaction();
        crt.loadFiles(null, new String[]{"myFile.java", "file2.java"});

        f1.delete();
        f2.delete();
        crt.getStdoutLines().add("compile diagnostics");
        crt.getStderrLines().add("stderr message");
        sendTransaction(crt);

        fileReceived = true;
      } catch (IOException e) {
      }
    }

    public void process(LoginTransaction t) throws Exception {
      LoginResponseTransaction lrt = new LoginResponseTransaction(true);
      sendTransaction(lrt);
      isLoggedIn = true;
    }
  }

  public SocketTransactionProcessor getProcessor() {
    return new RemoteCompilerTransactionProcessor();
  }
}

public class TestRemoteCompiler extends TestClientBase {
  public static void main(String[] args) {
    TestRunner.main(new String[]{"com.objectmentor.SMCRemote.client.TestRemoteCompiler"});
  }

  public TestRemoteCompiler(String name) {
    super(name);
  }

  private RemoteCompiler c;
  private MockRemoteCompilerServer server;
  private SocketService smc;

  public void setUp() throws Exception {
    super.setUp();
    c = new RemoteCompiler("localhost", SMCPORT, new NullMessageLogger());
    server = new MockRemoteCompilerServer();
    smc = new SocketService(SMCPORT, server);
  }

  public void tearDown() throws Exception {
    c.close();
    smc.close();
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
    assertEquals("Bad Generator", "java", server.generator);
    f.delete();
  }

  public void testMainCompileFile() throws Exception {
    File f = createTestFile("myFile.sm", "the content");
    runMain(new String[]{"-u", "user", "-w", "pw", "-g", "C++", "myFile.sm"});

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

    assert("consoleMessage", getStdout().startsWith("compile diagnostics"));
    assert("Stderr Message", getStderr().startsWith("stderr message"));
  }
}