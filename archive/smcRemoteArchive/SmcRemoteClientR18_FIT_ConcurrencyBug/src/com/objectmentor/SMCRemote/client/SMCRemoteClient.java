package com.objectmentor.SMCRemote.client;

public class SMCRemoteClient implements ClientCommandLineProcessor {
  public static final String VERSION = "$Id: SMCRemoteClient.java,v 1.10 2002/10/28 02:00:40 Administrator Exp $";

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
      System.out.println("usage: ");
      System.out.println("  to compile:  java SMCRemoteClient -u <emailaddress> -w <password> <filename>");
      System.out.println("  to register: java SMCRemoteClient -r <emailaddress>");
      System.out.println("options: -h <hostname>   override default hostname.");
      System.out.println("         -p <port>       override default port");
      System.out.println("         -v              verbose console output");
    }
  }

  public void setGenericParameters(String host, int port, boolean verbose) {
    itsHost = host;
    itsPort = port;
    isVerbose = verbose;
  }

  public void compile(String username, String password, String generator, String filename) {
    RemoteCompiler compiler = new RemoteCompiler(itsHost, itsPort, itsLogger);
    compiler.compile(username, password, generator, filename);
  }

  public void register(String registrant) {
    RemoteRegistrar registrar = new RemoteRegistrar(itsHost, itsPort, itsLogger);
    registrar.connectAndRegister(registrant);
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
