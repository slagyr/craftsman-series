package com.objectmentor.SMCRemote.client;

import com.objectmentor.SMCRemote.transactions.*;
import com.objectmentor.SocketUtilities.SocketTransaction;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class SMCRemoteClient {

  private String itsFilename = null;
  private Socket smcrSocket;
  private ObjectInputStream is;
  private ObjectOutputStream os;

  public static void main(String[] args) {
    SMCRemoteClient client = new SMCRemoteClient();
    client.setFilename(args[0]);
    if (client.prepareFile())
      if (client.connect()) {
        CompilerResultsTransaction crt = client.compileFile(args);
        if (crt != null)
          writeCompilerOutputLines(crt);
        else
          System.out.println("failed to compile");
        client.close();
      } else  // connect
        System.out.println("failed to connect");
    else { // prepareFile
      System.out.println("could not open " + args[0]);
    }
  }

  public boolean parseCommandLine(String[] args) {
    try {
      itsFilename = args[0];
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public String filename() {
    return itsFilename;
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

  public boolean connect() {
    boolean connectionStatus = false;
    try {
      smcrSocket = new Socket("localhost", 9000);
      is = new ObjectInputStream(smcrSocket.getInputStream());
      os = new ObjectOutputStream(smcrSocket.getOutputStream());
      String headerLine = (String) is.readObject();
      connectionStatus = headerLine != null && headerLine.startsWith("SMCR");
    } catch (Exception e) {
      connectionStatus = false;
    }
    return connectionStatus;
  }

  public void close() {
    try {
      if (is != null) is.close();
      if (os != null) os.close();
      if (smcrSocket != null) smcrSocket.close();
    } catch (IOException e) {
    }
  }

  private boolean sendTransaction(SocketTransaction t) {
    boolean sent = false;
    try {
      os.writeObject(t);
      os.flush();
      sent = true;
    } catch (IOException e) {
      sent = false;
    }
    return sent;
  }

  public CompilerResultsTransaction compileFile(String[] args) {
    CompilerResultsTransaction crt = null;
    try {
      CompileFileTransaction t = new CompileFileTransaction(args);
      if (sendTransaction(t) == true) {
        crt = (CompilerResultsTransaction) is.readObject();
        crt.write();
      }
    } catch (Exception e) {
      e.printStackTrace();
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

}
