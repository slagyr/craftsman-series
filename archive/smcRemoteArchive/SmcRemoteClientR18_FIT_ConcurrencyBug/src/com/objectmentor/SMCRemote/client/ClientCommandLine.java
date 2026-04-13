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
  private String itsUsername;
  private String itsPassword;
  private boolean isValid = false;
  private Getopts opts;

  public ClientCommandLine(String[] args) {
    isValid = parseCommandLine(args);
  }

  public boolean isValid() {
    return isValid;
  }

  public boolean parseCommandLine(String[] args) {
    opts = new Getopts("r:h:p:g:u:w:v", args);
    if (opts.error()) return false;

    try {
      itsFilename = opts.argv(0);
      itsHost = opts.option('h', DEFAULT_HOST);
      itsPort = Integer.parseInt(opts.option('p', DEFAULT_PORT));
      itsGenerator = opts.option('g', DEFAULT_GENERATOR);
      itsRegistrant = opts.option('r', null);
      itsUsername = opts.option('u', null);
      itsPassword = opts.option('w', null);
      isVerbose = opts.hasOption('v');
    } catch (NumberFormatException e) {
      return false;
    }

    return isCompileCommand() || isRegistrationCommand();
  }

  public void setGenericParameters(ClientCommandLineProcessor processor) {
    processor.setGenericParameters(itsHost, itsPort, isVerbose);
  }

  public void processCommand(ClientCommandLineProcessor processor) {
    if (isCompileCommand()) {
      processor.compile(itsUsername, itsPassword, itsGenerator, itsFilename);
    } else if (isRegistrationCommand()) {
      processor.register(itsRegistrant);
    }
  }

  private boolean hasFileName() {
    return opts.argc() == 1;
  }

  private boolean isCompileCommand() {
    return opts.hasOption('u') &&
      opts.hasOption('w') &&
      !opts.hasOption('r') &&
      hasFileName();
  }

  private boolean isRegistrationCommand() {
    return opts.hasOption('r') &&
      (itsRegistrant != null) &&
      !opts.hasOption('u') &&
      !opts.hasOption('w') &&
      !opts.hasOption('g') &&
      !hasFileName();
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

  public String getUsername() {
    return itsUsername;
  }

  public String getPassword() {
    return itsPassword;
  }

}
