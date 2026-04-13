package com.objectmentor.SMCRemote.server;

import com.objectmentor.SMCRemote.transactions.CompileFileTransaction;
import com.objectmentor.SMCRemote.transactions.CompilerResultsTransaction;
import junit.framework.TestCase;
import junit.swingui.TestRunner;

import java.io.*;
import java.net.Socket;
import java.util.Vector;
import java.util.Iterator;

public class TestSMCRemoteServer extends TestCase {

  public void testBuildCommand() throws Exception {
    assertEquals("BuildCommand",
                 "java -cp c:\\SMC\\smc.jar smc.Smc -f this that the other",
                 SMCRemoteServer.buildCommand(new String[]{"this",
                                                           "that",
                                                           "the",
                                                           "other"}));
  }

  public void testExecuteCommand() throws Exception {
    File smFile = new File("myFile.sm");
    File javaFile = new File("F.java");

    writeSourceFile(smFile);
    Vector stdout = new Vector();
    Vector stderr = new Vector();
    assertEquals("exitValue", 0, SMCRemoteServer.executeCommand("java -cp c:\\SMC\\smc.jar smc.Smc -f myFile.sm",stdout, stderr));
    assertEquals("fileExists", true, javaFile.exists());
    assertTrue("javaFile", javaFile.delete());
    assertTrue("smFile", smFile.delete());
    assertTrue("stdout empty", stdout.size() > 0);
    assertTrue("stderr not empty", stderr.size() == 0);
  }

  private void writeSourceFile(File smFile) throws IOException {
    PrintWriter w = new PrintWriter(new FileWriter(smFile));
    w.println("Context C");
    w.println("FSMName F");
    w.println("Initial I");
    w.println("{I{E I A}}");
    w.close();
  }

  public void testMakeTempDirectory() throws Exception {
    File f1 = SMCRemoteServer.makeTempDirectory();
    File f2 = SMCRemoteServer.makeTempDirectory();
    assertEquals("MakeTempDirectory", false, f1.getName().equals(f2.getName()));
    assertTrue("f1",f1.delete());
    assertTrue("f2",f2.delete());
  }

  public void testCompile() throws Exception {
    boolean javaFileExists = false;

    SMCRemoteServer server = new SMCRemoteServer(999);

    Socket client = new Socket("localhost", 999);
    ObjectInputStream is = new ObjectInputStream(client.getInputStream());
    ObjectOutputStream os = new ObjectOutputStream(client.getOutputStream());

    String headerLine = (String) is.readObject();
    assertTrue("headerline", headerLine.startsWith("SMCR Server"));

    File sourceFile = new File("myFile.sm");
    writeSourceFile(sourceFile);

    CompileFileTransaction cft = new CompileFileTransaction(new String[]{"myFile.sm"});
    os.writeObject(cft);
    os.flush();
    CompilerResultsTransaction crt = (CompilerResultsTransaction) is.readObject();

    Thread.sleep(500);
    client.close();
    server.close();

    String filenames[] = crt.getFilenames();
    assertEquals("filenames", 1, filenames.length);
    assertEquals("F.java", "F.java", filenames[0]);

    crt.write();

    File javaFile = new File("F.java");
    assertEquals("Compile", true, javaFile.exists());
    javaFile.delete();
    sourceFile.delete();

    Vector stdout = crt.getStdout();
    Vector stderr = crt.getStderr();
    assertTrue("no stdout",stdout != null);
    assertTrue("no stderr",stderr != null);

    assertTrue("stdout empty", stdout.size() > 0);
    assertTrue("stderr not empty", stderr.size() == 0);

    for (Iterator i = stdout.iterator(); i.hasNext();) {
      String s = (String) i.next();
      System.out.println(s);
    }
  }
}
