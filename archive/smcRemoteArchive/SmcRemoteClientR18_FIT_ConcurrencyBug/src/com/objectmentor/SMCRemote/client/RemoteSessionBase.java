package com.objectmentor.SMCRemote.client;

import com.objectmentor.SMCRemote.transactions.*;

import java.io.*;
import java.net.Socket;

public class RemoteSessionBase {
  private String itsHost;
  private int itsPort;
  private MessageLogger itsLogger;
  private Socket smcrSocket;
  private ObjectInputStream is;
  private ObjectOutputStream os;

  public RemoteSessionBase(String itsHost, int itsPort, MessageLogger logger) {
    this.itsHost = itsHost;
    this.itsPort = itsPort;
    this.itsLogger = logger;
  }

  public String getHost() {
    return itsHost;
  }

  public int getPort() {
    return itsPort;
  }

  protected void logMessage(String msg) {
    itsLogger.logMessage(msg);
  }

  protected boolean connect() {
    logMessage("Trying to connect to: " + getHost() + ":" + getPort() + "...");
    boolean connectionStatus = false;
    try {
      smcrSocket = new Socket(getHost(), getPort());
      is = new ObjectInputStream(smcrSocket.getInputStream());
      os = new ObjectOutputStream(smcrSocket.getOutputStream());
      String headerLine = (String) readServerObject();
      connectionStatus = headerLine != null && headerLine.startsWith("SMCR");
      String message = (String) readServerObject();
      if (message != null) {
        System.out.println(message);
      }

      if (connectionStatus)
        logMessage("Connection acknowledged: " + headerLine);
      else
        logMessage("Bad Acknowledgement: " + headerLine);
    } catch (Exception e) {
      connectionStatus = false;
      logMessage("Connection failed: " + e.getMessage());
    }
    return connectionStatus;
  }

  public void close() {
    if (is != null || os != null || smcrSocket != null) {
      logMessage("Closing Connection.");
      try {
        if (is != null) is.close();
        if (os != null) os.close();
        if (smcrSocket != null) smcrSocket.close();
      } catch (IOException e) {
        logMessage("Couldn't close : " + e.getMessage());
      }
    }
  }

  protected boolean login(String username, String password) {
    try {
      LoginTransaction lt = new LoginTransaction(username, password);
      sendTransaction(lt);
      LoginResponseTransaction lrt = (LoginResponseTransaction) readServerObject();
      if (lrt.isAccepted()) {
        logMessage("login (" + lrt.getLoginCount() + ") accepted.");
        return true;
      } else {
        logMessage("Login Rejected");
        return false;
      }
    } catch (Exception e) {
      logMessage("login failed: " + e);
      return false;
    }
  }

  protected boolean sendTransaction(SocketTransaction t) {
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

  protected Object readServerObject() throws Exception {
    return is.readObject();
  }
}
