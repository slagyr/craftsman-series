package com.objectmentor.SMCRemote.server;

import com.objectmentor.SMCRemote.transactions.*;
import com.objectmentor.SocketService.*;

import com.neoworks.util.Getopts;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class SMCRemoteService {
  public static final String DEFAULT_PORT = "9000";
  public static final String VERSION = "$Id: SMCRemoteService.java,v 1.6 2002/10/06 18:57:02 Administrator Exp $";
  public static int servicePort;
  public static boolean isVerbose = false;

  private SocketService service;

  public static final String COMPILE_COMMAND = "java -cp c:\\SMC\\smc.jar smc.Smc -f";

  public SMCRemoteService(int port) throws Exception {
    service = new SocketService(port, new SMCRemoteServer());
  }

  public void close() throws Exception {
    service.close();
  }

  public static void main(String[] args) {
    if (parseCommandLine(args)) {
      verboseHeader();
      try {
        SMCRemoteService service = new SMCRemoteService(servicePort);
      } catch (Exception e) {
        System.err.println("Could not connect");
      }
    } else {
      System.out.println("usage: java SMCRemoteService -p <port> -v");
    }
  }

  static boolean parseCommandLine(String[] args) {
    Getopts opts = new Getopts("p:v", args);
    if (opts.error())
      return false;

    try {
      servicePort = Integer.parseInt(opts.option('p', DEFAULT_PORT));
      isVerbose = opts.hasOption('v');
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

class SMCRemoteServer implements SocketServer {
  private ObjectOutputStream serverOutput;
  private ObjectInputStream serverInput;
  private CompileFileTransaction cft;
  private File tempDirectory;
  private CompilerResultsTransaction crt;
  private String itsSessionID;

  public void serve(Socket socket) {
    buildSessionID(socket);
    verboseMessage("Connected");
    try {
      initializeServer(socket);
      cft = (CompileFileTransaction) serverInput.readObject();
      verboseMessage("Compiling: " + cft.getFilename() + " using " + cft.getGenerator() + " generator.");

      tempDirectory = SMCRemoteService.makeTempDirectory();
      cft.write(tempDirectory);

      compile();
      sendCompilerResult();

      tempDirectory.delete();
      socket.close();
      verboseMessage("Connection closed.");
    } catch (Exception e) {
    }
  }

  private void buildSessionID(Socket socket) {
    InetAddress addr = socket.getInetAddress();
    String connectedHostName = addr.getHostName();
    String connectedIP = addr.getHostAddress();
    if (connectedHostName.equals(connectedIP)) {
      itsSessionID = connectedHostName + ":" + socket.getPort();
    } else {
      itsSessionID = connectedHostName + ":" + socket.getPort() + "(" + connectedIP + ")";
    }
  }

  private void compile() throws Exception {
    String filename = cft.getFilename();
    crt = new CompilerResultsTransaction();
    File batFile = writeCompileScript();

    SMCRemoteService.executeCommand(tempDirectory + "\\smc.bat", crt.getStdoutLines(), crt.getStderrLines());

    batFile.delete();
    File sourceFile = new File(tempDirectory, filename);
    sourceFile.delete();
  }

  private File writeCompileScript() throws IOException {
    File batFile = new File(tempDirectory, "smc.bat");
    PrintWriter bat = new PrintWriter(new FileWriter(batFile));
    bat.println("cd " + tempDirectory);
    bat.println(SMCRemoteService.buildCommand(cft.getFilename(), cft.getGenerator()));
    bat.close();
    return batFile;
  }

  private void sendCompilerResult() throws IOException {
    String[] filenames = tempDirectory.list();
    crt.loadFiles(tempDirectory, filenames);
    verboseSendFileReport(filenames);
    serverOutput.writeObject(crt);
    serverOutput.flush();
    deleteCompiledFiles(filenames);
  }

  private void deleteCompiledFiles(String[] filenames) {
    for (int i = 0; i < filenames.length; i++) {
      File f = new File(tempDirectory, filenames[i]);
      f.delete();
    }
  }

  private void initializeServer(Socket socket) throws IOException {
    serverOutput = new ObjectOutputStream(socket.getOutputStream());
    serverInput = new ObjectInputStream(socket.getInputStream());
    serverOutput.writeObject("SMCR Server. " + SMCRemoteService.VERSION);
    serverOutput.flush();
  }

  private void verboseSendFileReport(String[] filenames) {
    for (int i = 0; i < filenames.length; i++) {
      String filename = filenames[i];
      verboseMessage("Sending: " + filename);
    }
  }

  private void verboseMessage(String msg) {
    SMCRemoteService.verboseMessage("<" + itsSessionID + "> " + msg);
  }

}
