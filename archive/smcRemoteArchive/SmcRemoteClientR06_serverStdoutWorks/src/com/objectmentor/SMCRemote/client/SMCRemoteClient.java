package com.objectmentor.SMCRemote.client;

import com.objectmentor.SMCRemote.transactions.CompileFileTransaction;
import com.objectmentor.SMCRemote.transactions.CompilerResultsTransaction;
import com.objectmentor.SocketUtilities.SocketTransaction;

import java.io.*;
import java.net.Socket;

public class SMCRemoteClient {

  private String itsFilename = null;
  private int itsFileLength = 0;
  private Socket smcrSocket;
  private ObjectInputStream is;
  private ObjectOutputStream os;

  public static void main(String[] args) {
    SMCRemoteClient client = new SMCRemoteClient();
    client.setFilename(args[0]);
    if (client.prepareFile())
      if (client.connect())
        if (client.compileFile(args))
          client.close();
        else {  // compileFile
          System.out.println("failed to compile");
        }
      else { // connect
        System.out.println("failed to connect");
      }
    else { // prepareFile
      System.out.println("failed to prepare");
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
      itsFileLength = (int) f.length();
      filePrepared = true;
    }
    return filePrepared;
  }

  public int getFileLength() {
    return itsFileLength;
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

  public boolean compileFile(String[] args) {
    boolean compiled = false;
    char buffer[] = new char[itsFileLength];
    try {
      CompileFileTransaction t = new CompileFileTransaction(args);
      if (sendTransaction(t) == true) {
        CompilerResultsTransaction crt = (CompilerResultsTransaction) is.readObject();
        crt.write();
        compiled = true;
      } else
        compiled = false;
    } catch (Exception e) {
      e.printStackTrace();
      compiled = false;
    }

    return compiled;
  }

}
