package com.objectmentor.SMCRemote.client;

import com.objectmentor.SocketService.SocketServer;
import com.objectmentor.SocketService.SocketService;
import junit.framework.TestCase;
import junit.swingui.TestRunner;

import java.io.*;
import java.net.Socket;

import com.objectmentor.SocketUtilities.SocketTransaction;
import com.objectmentor.SMCRemote.transactions.SocketTransactionProcessor;
import com.objectmentor.SMCRemote.transactions.CompileFileTransaction;
import com.objectmentor.SMCRemote.transactions.CompilerResultsTransaction;

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
        TestSMCRemoteClient.createTestFile("myFile.java", "wow");
        TestSMCRemoteClient.createTestFile("file2.java", "ick");
        filename = t.getFileName();
        command = t.getArgs();

        CompilerResultsTransaction crt = new CompilerResultsTransaction();
        crt.loadFiles(new String[]{"myFile.java"});
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
    assertTrue("result should be false", !result);
  }

  public void testCountBytesInFile() throws Exception {
    File f = createTestFile("testFile", "some text");
    c.setFilename("testFile");
    boolean prepared = c.prepareFile();
    assertTrue("file not deleted", f.delete());
    assertTrue(prepared);
    assertEquals(9, c.getFileLength());
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
    assertTrue(connection);
  }

  public void testCompileFile() throws Exception {
    String filename = "testSendFile";
    String args[] = new String[]{filename, "hay", "dere", "Eh?"};
    File f = createTestFile(filename, "I am sending this file.");
    c.setFilename(filename);
    assertTrue(c.connect());
    assertTrue(c.prepareFile());
    assertTrue(c.compileFile(args));
    Thread.sleep(50);
    assertTrue(server.fileReceived);
    assertEquals(filename, server.filename);
    f.delete();
    assertEquals("", filename, server.command[0]);
    assertEquals("", "hay", server.command[1]);
    assertEquals("", "dere", server.command[2]);
    assertEquals("", "Eh?", server.command[3]);
  }

  public void testMain() throws Exception {
    File f = createTestFile("myFile.sm", "the content");
    SMCRemoteClient.main(new String[]{"myFile.sm"});
    Thread.yield();
    f.delete();
    File file1 = new File("myFile.java");
    File file2 = new File("file2.java");
    boolean file1Exists = file1.exists();
    boolean file2Exists = file2.exists();
    boolean exists = file1Exists && file2Exists;
    if (file1Exists) file1.delete();
    if (file2Exists) file2.delete();

    assertTrue("not received", server.fileReceived);
    assertTrue("One or more files doesn't exist", exists);
  }
}