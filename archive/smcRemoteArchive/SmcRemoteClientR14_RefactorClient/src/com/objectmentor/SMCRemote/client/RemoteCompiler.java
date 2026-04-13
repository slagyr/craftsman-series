package com.objectmentor.SMCRemote.client;

import com.objectmentor.SMCRemote.transactions.*;

import java.io.*;
import java.util.Vector;

public class RemoteCompiler extends RemoteSessionBase {

  private String itsFilename = null;
  private String itsGenerator = ClientCommandLine.DEFAULT_GENERATOR;
  private String itsRegistrant;

  public RemoteCompiler(String itsHost, int itsPort, MessageLogger itsLogger) {
    super(itsHost, itsPort, itsLogger);
  }

  public void compile(String generator, String filename) {
    itsFilename = filename;
    itsGenerator = generator;
    logCompileHeader();
    connectAndRequestCompile();
  }

  private void connectAndRequestCompile() {
    if (prepareFile()) {
      if (connect()) {
        if (login()) {
          if (compile() == false) {
            System.out.println("Internal error, something awful.  Sorry.");
          }
        } else { // login
          System.out.println("failed to log in.");
        }
        close();
      } else { // connect
        System.out.println("failed to connect to " + getHost() + ":" + getPort());
      }
    } else { // prepareFile
      System.out.println("could not open: " + itsFilename);
    } // prepareFile
  }

  private boolean compile() {
    CompilerResultsTransaction crt = compileFile();
    if (crt == null)
      return false;
    writeCompilerOutputLines(crt);
    return true;
  }

  public void setFilename(String itsFilename) {
    this.itsFilename = itsFilename;
  }

  public boolean prepareFile() {
    boolean filePrepared = false;
    FileInputStream is = null;
    File f = new File(itsFilename);
    if (f.exists()) {
      filePrepared = true;
    }
    return filePrepared;
  }

  public CompilerResultsTransaction compileFile() {
    CompilerResultsTransaction crt = null;
    logMessage("Sending file and requesting compilation.");
    try {
      CompileFileTransaction t = new CompileFileTransaction(itsFilename, itsGenerator);
      if (sendTransaction(t) == true) {
        crt = (CompilerResultsTransaction) readServerObject();
        logCompilerResultsMessage(crt);
        crt.write();
      }
    } catch (Exception e) {
      logMessage("Compilation process failed: " + e.getMessage());
    }

    return crt;
  }

  private static void writeCompilerOutputLines(CompilerResultsTransaction crt) {
    Vector stdout = crt.getStdoutLines();
    writeLineVector(System.out, crt.getStdoutLines());
    writeLineVector(System.err, crt.getStderrLines());
  }

  private static void writeLineVector(PrintStream ps, Vector stdout) {
    for (int i = 0; i < stdout.size(); i++) {
      String s = (String) stdout.elementAt(i);
      ps.println(s);
    }
  }

  private void logCompileHeader() {
    logMessage("Compiling...");
    logMessage("file =      " + itsFilename);
    logMessage("generator = " + itsGenerator);
  }

  private void logCompilerResultsMessage(CompilerResultsTransaction crt) {
    logMessage("Compilation results received.");
    String filenames[] = crt.getFilenames();
    for (int i = 0; i < filenames.length; i++) {
      String s = (String) filenames[i];
      logMessage("..file: " + s + " received.");
    }
  }

}

