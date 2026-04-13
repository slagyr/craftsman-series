package com.objectmentor.SMCRemote.server;

import com.objectmentor.SocketService.SocketService;

import com.neoworks.util.Getopts;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SMCRemoteService {
  public static final String DEFAULT_PORT = "9000";
  public static final String VERSION = "0.99";
  public static final String COMPILE_COMMAND = "java -cp c:\\SMC\\smc.jar smc.Smc -f";

  static boolean isVerbose = false;
  static int servicePort;
  static String messageFile;

  private static UserDirectory userDirectory = new UserDirectory() {
    public boolean isValid(String username, String password) {
      return true;
    }

    public String getPassword(String username) {
      return null;
    }

    public boolean add(String username, String password) {
      return true;
    }
  };

  private static EmailSender emailSender = new EmailSender() {
    public boolean send(String emailAddress, String subject, String text) {
      return false;
    }
  };

  private SocketService service;

  public SMCRemoteService(int port) throws Exception {
    service = new SocketService(port, new SMCRemoteServer());
  }

  public void close() throws Exception {
    service.close();
  }

  static void setUserDirectory(UserDirectory userDirectory) {
    SMCRemoteService.userDirectory = userDirectory;
  }

  public static void setEmailSender(EmailSender emailSender) {
    SMCRemoteService.emailSender = emailSender;
  }

  public static void main(String[] args) {
    if (parseCommandLine(args)) {
      verboseHeader();
      setUserDirectory(new UserRepository("users"));
      setEmailSender(new OReillyEmailSender());
      try {
        SMCRemoteService service = new SMCRemoteService(servicePort);
      } catch (Exception e) {
        System.err.println("Could not connect");
      }
    } else {
      System.out.println("usage: java SMCRemoteService -p <port> -v");
    }
  }

  static boolean validate(String username, String password) {
    return userDirectory.isValid(username, password);
  }

  static boolean addUser(String username, String password) throws Exception {
    return userDirectory.add(username, password);
  }

  static String getPassword(String username) {
    return userDirectory.getPassword(username);
  }

  static boolean sendEmail(String emailAddress, String subject, String text) {
    return emailSender.send(emailAddress, subject, text);
  }

  static boolean parseCommandLine(String[] args) {
    Getopts opts = new Getopts("m:p:v", args);
    if (opts.error())
      return false;

    try {
      servicePort = Integer.parseInt(opts.option('p', DEFAULT_PORT));
      isVerbose = opts.hasOption('v');
      messageFile = opts.option('m', null);
    } catch (NumberFormatException e) {
      return false;
    }

    return true;
  }

  static String buildCommand(String filename, String generator) {
    String generatorClass;

    if (generator.equals("java"))
      generatorClass = "smc.generator.java.SMJavaGenerator";
    else if (generator.equals("C++"))
      generatorClass = "smc.generator.cpp.SMCppGenerator";
    else
      return "echo bad generator " + generator;

    return COMPILE_COMMAND + " -g " + generatorClass + " " + filename;
  }

  static int executeCommand(String command, Vector stdout, Vector stderr) throws Exception {
    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec(command);
    flushProcessOutputs(p, stdout, stderr);
    p.waitFor();

    return p.exitValue();
  }

  private static void flushProcessOutputs(Process p, Vector stdout, Vector stderr) throws IOException {
    BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    BufferedReader stderrReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    String line;

    while ((line = stdoutReader.readLine()) != null)
      stdout.add(line);
    while ((line = stderrReader.readLine()) != null)
      stderr.add(line);
  }

  static File makeTempDirectory() {
    File tmpDirectory;
    do {
      long millis = System.currentTimeMillis();
      tmpDirectory = new File("smcTempDirectory" + millis);
    } while (tmpDirectory.exists());
    tmpDirectory.mkdir();
    return tmpDirectory;
  }

  private static void verboseHeader() {
    verboseMessage("SMCRemoteService---------------------------------");
    verboseMessage(VERSION);
    verboseMessage("port = " + servicePort);
    verboseMessage("-------------------------------------------------");
  }

  static void verboseMessage(String msg) {
    if (isVerbose) {
      Date logTime = new Date();
      SimpleDateFormat fmt = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
      String logTimeString = fmt.format(logTime);

      System.out.println(logTimeString + " | " + msg);
    }
  }
}


