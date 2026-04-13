package com.objectmentor.SMCRemote.server;

import com.objectmentor.SMCRemote.transactions.CompileFileTransaction;
import com.objectmentor.SMCRemote.transactions.CompilerResultsTransaction;
import com.objectmentor.SocketService.SocketServer;
import com.objectmentor.SocketService.SocketService;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class SMCRemoteServer {
  private SocketService service;
  private ObjectOutputStream serverOutput;
  private ObjectInputStream serverInput;

  public SMCRemoteServer(int port) throws Exception {
    service = new SocketService(999, new SocketServer() {
      public void serve(Socket socket) {
        serveSMC(socket);
      }
    });
  }

  public void close() {
  }

  private void serveSMC(Socket socket) {
    try {
      initializeServer(socket);
      CompileFileTransaction cft = (CompileFileTransaction) serverInput.readObject();

      File tempDirectory = makeTempDirectory();
      cft.write(tempDirectory);

      CompilerResultsTransaction crt = compile(tempDirectory, cft.getArgs());
      sendCompilerResult(crt, tempDirectory);

      tempDirectory.delete();
      socket.close();
    } catch (Exception e) {
    }
  }

  private CompilerResultsTransaction compile(File tempDirectory, String[] args) throws Exception {
    CompilerResultsTransaction crt = new CompilerResultsTransaction();
    File batFile = new File(tempDirectory, "smc.bat");
    PrintWriter bat = new PrintWriter(new FileWriter(batFile));
    bat.println("cd " + tempDirectory);
    bat.println(buildCommand(args));
    bat.close();

    executeCommand(tempDirectory + "\\smc.bat", crt.getStdout(), crt.getStderr());

    batFile.delete();
    File sourceFile = new File(tempDirectory, args[0]);
    sourceFile.delete();
    return crt;
  }

  private void sendCompilerResult(CompilerResultsTransaction crt, File tempDirectory) throws IOException {
    String[] filenames = tempDirectory.list();
    crt.loadFiles(filenames);
    serverOutput.writeObject(crt);
    serverOutput.flush();

    for (int i = 0; i < filenames.length; i++) {
      File f = new File(tempDirectory, filenames[i]);
      f.delete();
    }
  }

  private void initializeServer(Socket socket) throws IOException {
    serverOutput = new ObjectOutputStream(socket.getOutputStream());
    serverInput = new ObjectInputStream(socket.getInputStream());
    serverOutput.writeObject("SMCR Server.  $Id: SMCRemoteServer.java,v 1.1 2002/09/24 01:05:14 rmartin Exp $");
    serverOutput.flush();
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

  static String buildCommand(String[] args) {
    StringBuffer command = new StringBuffer("java -cp c:\\SMC\\smc.jar smc.Smc -f");
    for (int i = 0; i < args.length; i++) {
      command.append(" " + args[i]);
    }
    return command.toString();
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
}
