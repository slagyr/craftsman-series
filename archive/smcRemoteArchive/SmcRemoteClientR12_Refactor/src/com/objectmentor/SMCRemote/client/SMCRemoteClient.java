package com.objectmentor.SMCRemote.client;

import com.objectmentor.SMCRemote.transactions.*;

import com.neoworks.util.Getopts;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class SMCRemoteClient {
  public static final String DEFAULT_HOST = "localhost";
  public static final String DEFAULT_PORT = "9000";
  public static final String DEFAULT_GENERATOR = "java";
  public static final String VERSION = "$Id: SMCRemoteClient.java,v 1.7 2002/10/11 00:49:32 Administrator Exp $";

  private String itsFilename = null;
  private String itsHost = DEFAULT_HOST;
  private int itsPort = Integer.parseInt(DEFAULT_PORT);
  private String itsGenerator = DEFAULT_GENERATOR;
  private boolean isVerbose = false;

  private Socket smcrSocket;
  private ObjectInputStream is;
  private ObjectOutputStream os;

  public static void main(String[] args) {
    SMCRemoteClient client = new SMCRemoteClient();
    if (client.parseCommandLine(args)) {
      client.verboseHeader();
      if (client.prepareFile()) {
        if (client.connect()) {
          if (client.login()) {
            CompilerResultsTransaction crt = client.compileFile();
            if (crt != null) {
              writeCompilerOutputLines(crt);
            } else {
              System.out.println("Internal error, something awful.  Sorry.");
            }
          } else { // login
            System.out.println("failed to log in.");
          }
          client.close();
        } else { // connect
          System.out.println("failed to connect to " + client.getHost() + ":" + client.getPort());
        }
      } else { // prepareFile
        System.out.println("could not open: " + client.getFilename());
      } // prepareFile
    } else { //parseCommandLine
      System.out.println("usage: java SMCRemoteClient [-v] [-h <host>] [-p <port>] <filename>");
    }
  }

  public boolean isVerbose() {
    return isVerbose;
  }

  public String getHost() {
    return itsHost;
  }

  public String getFilename() {
    return itsFilename;
  }

  public int getPort() {
    return itsPort;
  }

  public String getGenerator() {
    return itsGenerator;
  }

  public void setFilename(String itsFilename) {
    this.itsFilename = itsFilename;
  }

  public boolean parseCommandLine(String[] args) {
    Getopts opts = new Getopts("h:p:g:v", args);
    if (opts.error()) return false;
    if (opts.argc() != 1) return false;

    try {
      itsFilename = opts.argv(0);
      itsHost = opts.option('h', DEFAULT_HOST);
      itsPort = Integer.parseInt(opts.option('p', DEFAULT_PORT));
      itsGenerator = opts.option('g', DEFAULT_GENERATOR);
      isVerbose = opts.hasOption('v');
    } catch (NumberFormatException e) {
      return false;
    }

    return true;
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
    verboseMessage("Trying to connect to: " + getHost() + ":" + getPort() + "...");
    boolean connectionStatus = false;
    try {
      smcrSocket = new Socket(itsHost, itsPort);
      is = new ObjectInputStream(smcrSocket.getInputStream());
      os = new ObjectOutputStream(smcrSocket.getOutputStream());
      String headerLine = (String) is.readObject();
      connectionStatus = headerLine != null && headerLine.startsWith("SMCR");
      if (connectionStatus)
        verboseMessage("Connection acknowledged: " + headerLine);
      else
        verboseMessage("Bad Acknowledgement: " + headerLine);
    } catch (Exception e) {
      connectionStatus = false;
      verboseMessage("Connection failed: " + e.getMessage());
    }
    return connectionStatus;
  }

  public void close() {
    if (is != null || os != null || smcrSocket != null) {
      verboseMessage("Closing Connection.");
      try {
        if (is != null) is.close();
        if (os != null) os.close();
        if (smcrSocket != null) smcrSocket.close();
      } catch (IOException e) {
        verboseMessage("Couldn't close : " + e.getMessage());
      }
    }
  }

  private boolean login() {
    try {
      LoginTransaction lt = new LoginTransaction("username","password");
      sendTransaction(lt);
      LoginResponseTransaction lrt = (LoginResponseTransaction)is.readObject();
      verboseMessage("login accepted.");
      return true;
    } catch (Exception e) {
      verboseMessage("login failed: " + e);
      return false;
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

  public CompilerResultsTransaction compileFile() {
    CompilerResultsTransaction crt = null;
    verboseMessage("Sending file and requesting compilation.");
    try {
      CompileFileTransaction t = new CompileFileTransaction(itsFilename, itsGenerator);
      if (sendTransaction(t) == true) {
        crt = (CompilerResultsTransaction) is.readObject();
        verboseCompilerResultsMessage(crt);
        crt.write();
      }
    } catch (Exception e) {
      verboseMessage("Compilation process failed: " + e.getMessage());
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

  private void verboseHeader() {
    verboseMessage("SMCRemoteClient-------------------------------------");
    verboseMessage(VERSION);
    verboseMessage("host =      " + getHost());
    verboseMessage("port =      " + getPort());
    verboseMessage("file =      " + getFilename());
    verboseMessage("generator = " + getGenerator());
    verboseMessage("----------------------------------------------------");
  }

  private void verboseCompilerResultsMessage(CompilerResultsTransaction crt) {
    verboseMessage("Compilation results received.");
    String filenames[] = crt.getFilenames();
    for (int i = 0; i < filenames.length; i++) {
      String s = (String) filenames[i];
      verboseMessage("..file: " + s + " received.");
    }
  }

  private void verboseMessage(String msg) {
    if (isVerbose()) {
      Date logTime = new Date();
      SimpleDateFormat fmt = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
      String logTimeString = fmt.format(logTime);

      System.out.println(logTimeString + " | " + msg);
    }
  }
}
