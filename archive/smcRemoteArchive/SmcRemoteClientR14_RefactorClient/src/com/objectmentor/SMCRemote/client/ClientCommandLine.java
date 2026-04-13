package com.objectmentor.SMCRemote.client;

import com.neoworks.util.Getopts;

public class ClientCommandLine {
  public static final String DEFAULT_HOST = "localhost";
  public static final String DEFAULT_PORT = "9000";
  public static final String DEFAULT_GENERATOR = "java";

  private String itsFilename = null;
  private String itsHost = DEFAULT_HOST;
  private int itsPort = Integer.parseInt(DEFAULT_PORT);
  private String itsGenerator = DEFAULT_GENERATOR;
  private boolean isVerbose = false;
  private String itsRegistrant;
  private boolean isValid = false;
  private Getopts opts;

  public ClientCommandLine(String[] args) {
    isValid = parseCommandLine(args);
  }

  public boolean isValid() {
    return isValid;
  }

  public boolean parseCommandLine(String[] args) {
    opts = new Getopts("r:h:p:g:v", args);
    if (opts.error()) return false;

    try {
      itsFilename = opts.argv(0);
      itsHost = opts.option('h', DEFAULT_HOST);
      itsPort = Integer.parseInt(opts.option('p', DEFAULT_PORT));
      itsGenerator = opts.option('g', DEFAULT_GENERATOR);
      itsRegistrant = opts.option('r', null);
      isVerbose = opts.hasOption('v');
    } catch (NumberFormatException e) {
      return false;
    }

    if (isCompileCommand() && !hasFileName()) return false;
    return true;
  }

  public void setGenericParameters(ClientCommandLineProcessor processor) {
    processor.setGenericParameters(itsHost, itsPort, isVerbose);
  }

  public void processCommand(ClientCommandLineProcessor processor) {
    if (isCompileCommand()) {
      processor.compile(itsGenerator, itsFilename);
    }
    else if (isRegistrationCommand()) {
      processor.register(itsRegistrant);
    }
  }

  private boolean hasFileName() {
    return opts.argc() == 1;
  }

  private boolean isCompileCommand() {
    return !isRegistrationCommand();
  }

  private boolean isRegistrationCommand() {
    return opts.hasOption('r');
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

}
