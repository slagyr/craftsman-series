package com.objectmentor.SMCRemote.client;

public class SMCRemoteClient implements ClientCommandLineProcessor {
  public static final String VERSION = "$Id: SMCRemoteClient.java,v 1.9 2002/10/27 16:24:05 Administrator Exp $";

  private ClientCommandLine commandLine;
  private String itsHost;
  private int itsPort;
  private boolean isVerbose = false;
  private MessageLogger itsLogger;

  public static void main(String[] args) {
    SMCRemoteClient client = new SMCRemoteClient(args);
    client.run();
  }

  public SMCRemoteClient(String[] args) {
    commandLine = new ClientCommandLine(args);
    commandLine.setGenericParameters(this);
    if (isVerbose)
      itsLogger = new ConsoleMessageLogger();
    else
      itsLogger = new NullMessageLogger();
  }

  private void run() {
    if (commandLine.isValid()) {
      logHeader();
      commandLine.processCommand(this);
    } else {
      System.out.println("usage: java SMCRemoteClient [-v] [-h <host>] [-p <port>] <filename>");
    }
  }

  public void setGenericParameters(String host, int port, boolean verbose) {
    itsHost = host;
    itsPort = port;
    isVerbose = verbose;
  }

  public void compile(String generator, String filename) {
    RemoteCompiler compiler = new RemoteCompiler(itsHost, itsPort, itsLogger);
    compiler.compile(generator, filename);
  }

  public void register(String registrant) {
  }

  private void logHeader() {
    logMessage("SMCRemoteClient-------------------------------------");
    logMessage(VERSION);
    logMessage("host =      " + itsHost);
    logMessage("port =      " + itsPort);
    logMessage("----------------------------------------------------");
  }

  private void logMessage(String msg) {
    itsLogger.logMessage(msg);
  }
}
