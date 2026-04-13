package com.objectmentor.SMCRemote.server;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import com.objectmentor.SMCRemote.transactions.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class TestSMCRemoteServer extends TestCase {
  private ObjectInputStream is;
  private ObjectOutputStream os;
  private SMCRemoteService server;
  private Socket client;

  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestSMCRemoteServer"});
  }

  public TestSMCRemoteServer(String name) {
    super(name);
  }

  public void setUp() throws Exception {
  }

  public void tearDown() throws Exception {
  }

  public void testBuildCommand() throws Exception {
    assertEquals("Build Java Command",
                 SMCRemoteService.COMPILE_COMMAND + " -g smc.generator.java.SMJavaGenerator myFile",
                 SMCRemoteService.buildCommand("myFile", "java"));

    assertEquals("Build C++ Command",
                 SMCRemoteService.COMPILE_COMMAND + " -g smc.generator.cpp.SMCppGenerator myFile",
                 SMCRemoteService.buildCommand("myFile", "C++"));
  }

  public void testExecuteCommand() throws Exception {
    File smFile = new File("myFile.sm");
    File javaFile = new File("F.java");

    writeSourceFile(smFile);
    Vector stdout = new Vector();
    Vector stderr = new Vector();
    assertEquals("exitValue", 0, SMCRemoteService.executeCommand(SMCRemoteService.COMPILE_COMMAND + " myFile.sm", stdout, stderr));
    assertEquals("fileExists", true, javaFile.exists());
    assert("javaFile", javaFile.delete());
    assert("smFile", smFile.delete());
    checkCompilerOutputStreams(stdout, stderr);
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
    File f1 = SMCRemoteService.makeTempDirectory();
    File f2 = SMCRemoteService.makeTempDirectory();
    assertEquals("MakeTempDirectory", false, f1.getName().equals(f2.getName()));
    assert("f1", f1.delete());
    assert("f2", f2.delete());
  }

  public void testCompileJava() throws Exception {
    CompilerResultsTransaction crt = invokeRemoteCompiler("java");
    checkCompiledJavaFile(crt);
    checkCompilerOutputStreams(crt.getStdoutLines(), crt.getStderrLines());
  }

  public void testCompileCPP() throws Exception {
    CompilerResultsTransaction crt = invokeRemoteCompiler("C++");
    checkCompiledCPPFile(crt);
    checkCompilerOutputStreams(crt.getStdoutLines(), crt.getStderrLines());
  }

  private void checkCompilerOutputStreams(Vector stdout, Vector stderr) {
    assert("stdout empty", stdout.size() > 0);
    assert("stderr not empty", stderr.size() == 0);
  }

  private void checkCompiledJavaFile(CompilerResultsTransaction crt) {
    String filenames[] = crt.getFilenames();
    assertEquals("filenames", 1, filenames.length);
    assertEquals("F.java", "F.java", filenames[0]);

    crt.write();

    File javaFile = new File("F.java");
    assertEquals("Compile", true, javaFile.exists());
    javaFile.delete();
  }

  private void checkCompiledCPPFile(CompilerResultsTransaction crt) {
    String filenames[] = crt.getFilenames();
    Arrays.sort(filenames);
    assertEquals("filenames", 2, filenames.length);
    assertEquals("myFile.cpp", "myFile.cpp", filenames[0]);
    assertEquals("myFile.h", "myFile.h", filenames[1]);

    crt.write();

    File cppHFile = new File("myFile.h");
    File cppCFile = new File("myFile.cpp");
    assertEquals("Compile", true, cppHFile.exists() && cppCFile.exists());
    cppHFile.delete();
    cppCFile.delete();
  }

  private CompilerResultsTransaction invokeRemoteCompiler(String generator) throws Exception {
    ConnectClientToServer();
    CompileFileTransaction cft = buildCompileFileTransaction(generator);
    sendToServer(cft);
    CompilerResultsTransaction crt = (CompilerResultsTransaction) is.readObject();
    disconnectClientFromServer();
    return crt;
  }

  private void sendToServer(CompileFileTransaction cft) throws IOException {
    os.writeObject(cft);
    os.flush();
  }

  private void disconnectClientFromServer() throws Exception {
    Thread.sleep(500);
    client.close();
    server.close();
  }

  private CompileFileTransaction buildCompileFileTransaction(String generator) throws IOException {
    File sourceFile = new File("myFile.sm");
    writeSourceFile(sourceFile);
    CompileFileTransaction cft = new CompileFileTransaction("myFile.sm", generator);
    sourceFile.delete();
    return cft;
  }

  private void ConnectClientToServer() throws Exception {
    server = new SMCRemoteService(999);
    client = new Socket("localhost", 999);
    is = new ObjectInputStream(client.getInputStream());
    os = new ObjectOutputStream(client.getOutputStream());

    String headerLine = (String) is.readObject();
    assert("headerline", headerLine.startsWith("SMCR Server"));
  }

  public void testCloseServer() throws Exception {
    try {
      server = new SMCRemoteService(999);
      client = new Socket("localhost", 999);
      client.close();
      server.close();
    } catch (Exception e) {
      fail("couldn't connect" + e.getMessage());
    }
    try {
      client = new Socket("localhost", 999);
      fail("connected to closed server");
    } catch (Exception e) {
    }
  }
}
