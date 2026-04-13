package com.objectmentor.SMCRemote.server;

import junit.swingui.TestRunner;

import com.objectmentor.SMCRemote.transactions.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class TestCompilation extends TestBase {

  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestCompilation"});
  }

  public TestCompilation(String name) {
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
    String command = SMCRemoteService.COMPILE_COMMAND + " myFile.sm";
    assertEquals("exitValue", 0, SMCRemoteService.executeCommand(command, stdout, stderr));
    assertEquals("fileExists", true, javaFile.exists());
    assert("javaFile", javaFile.delete());
    assert("smFile", smFile.delete());
    checkCompilerOutputStreams(stdout, stderr);
  }

  public void testMakeTempDirectory() throws Exception {
    File f1 = SMCRemoteService.makeTempDirectory();
    File f2 = SMCRemoteService.makeTempDirectory();
    assertEquals("MakeTempDirectory", false, f1.getName().equals(f2.getName()));
    assert("f1", f1.delete());
    assert("f2", f2.delete());
  }

  public void testCompileJava() throws Exception {
    connectClientToServer();
    service.setUserDirectory(mockUserDirectory);
    login();
    CompilerResultsTransaction crt = invokeRemoteCompiler("java");
    assertEquals("Compiler Status", CompilerResultsTransaction.OK, crt.getStatus());
    checkCompiledJavaFile(crt);
    checkCompilerOutputStreams(crt.getStdoutLines(), crt.getStderrLines());
    disconnectClientFromServer();
  }

  public void testCompileCPP() throws Exception {
    connectClientToServer();
    service.setUserDirectory(mockUserDirectory);
    login();
    CompilerResultsTransaction crt = invokeRemoteCompiler("C++");
    assertEquals("Compiler Status", CompilerResultsTransaction.OK, crt.getStatus());
    checkCompiledCPPFile(crt);
    checkCompilerOutputStreams(crt.getStdoutLines(), crt.getStderrLines());
    disconnectClientFromServer();
  }

  public void testCompileNoLogin() throws Exception {
    connectClientToServer();
    service.setUserDirectory(mockUserDirectory);
    CompilerResultsTransaction crt = invokeRemoteCompiler("java");
    disconnectClientFromServer();
    assertEquals("Compiler Status", CompilerResultsTransaction.NOT_LOGGED_IN, crt.getStatus());
  }

  public void testTwoCompilesInARowNotAllowed() throws Exception {
    connectClientToServer();
    service.setUserDirectory(mockUserDirectory);
    login();
    CompilerResultsTransaction crt = invokeRemoteCompiler("java");
    try {
      crt = invokeRemoteCompiler("java");
      fail("Two Compiles in a row");
    } catch (Exception e) {
    } finally {
      disconnectClientFromServer();
    }
  }

  public void testCloseServer() throws Exception {
    try {
      service = new SMCRemoteService(999);
      client = new Socket("localhost", 999);
      client.close();
      service.close();
    } catch (Exception e) {
      fail("couldn't connect" + e.getMessage());
    }
    try {
      client = new Socket("localhost", 999);
      fail("connected to closed server");
    } catch (Exception e) {
    }
  }

  protected CompilerResultsTransaction invokeRemoteCompiler(String generator) throws Exception {
    CompileFileTransaction cft = buildCompileFileTransaction(generator);
    sendToServer(cft);
    CompilerResultsTransaction crt = (CompilerResultsTransaction) is.readObject();
    return crt;
  }

  protected CompileFileTransaction buildCompileFileTransaction(String generator) throws IOException {
    File sourceFile = new File("myFile.sm");
    writeSourceFile(sourceFile);
    CompileFileTransaction cft = new CompileFileTransaction("myFile.sm", generator);
    sourceFile.delete();
    return cft;
  }

  protected void writeSourceFile(File smFile) throws IOException {
    PrintWriter w = new PrintWriter(new FileWriter(smFile));
    w.println("Context C");
    w.println("FSMName F");
    w.println("Initial I");
    w.println("{I{E I A}}");
    w.close();
  }

  protected void checkCompilerOutputStreams(Vector stdout, Vector stderr) {
    assert("stdout empty", stdout.size() > 0);
    assert("stderr not empty", stderr.size() == 0);
  }

  protected void checkCompiledJavaFile(CompilerResultsTransaction crt) {
    String filenames[] = crt.getFilenames();
    assertEquals("filenames", 1, filenames.length);
    assertEquals("F.java", "F.java", filenames[0]);

    crt.write();

    File javaFile = new File("F.java");
    assertEquals("Compile", true, javaFile.exists());
    javaFile.delete();
  }

  protected void checkCompiledCPPFile(CompilerResultsTransaction crt) {
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

}
