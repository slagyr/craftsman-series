package com.objectmentor.SMCRemote.server;

import com.objectmentor.SMCRemote.transactions.CompileFileTransaction;
import com.objectmentor.SMCRemote.transactions.CompilerResultsTransaction;
import com.objectmentor.SocketService.SocketServer;
import com.objectmentor.SocketService.SocketService;

import java.io.*;
import java.net.Socket;

public class SMCRemoteServer {
  private SocketService service;
  ObjectOutputStream serverOutput;
  ObjectInputStream serverInput;

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

      compile(tempDirectory, cft.getArgs());
      sendCompilerResult(tempDirectory);

      tempDirectory.delete();
      socket.close();
    } catch (Exception e) {
    }
  }

  private void compile(File tempDirectory, String[] args) throws Exception {
    File batFile = new File(tempDirectory, "smc.bat");
    PrintWriter bat = new PrintWriter(new FileWriter(batFile));
    bat.println("cd " + tempDirectory);
    bat.println(buildCommand(args));
    bat.close();

    executeCommand(tempDirectory + "\\smc.bat");

    batFile.delete();
    File sourceFile = new File(tempDirectory, args[0]);
    sourceFile.delete();
  }

  private void sendCompilerResult(File tempDirectory) throws IOException {
    String[] filenames = tempDirectory.list();
    CompilerResultsTransaction crt = new CompilerResultsTransaction(filenames);
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

  static int executeCommand(String command) throws Exception {
    Runtime rt = Runtime.getRuntime();
    Process p = rt.exec(command);
    flushProcessOutputs(p);
    p.waitFor();

    return p.exitValue();
  }

  private static void flushProcessOutputs(Process p) throws IOException {
    BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
    BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    String line;

    while ((line = stdout.readLine()) != null)
      ;
    while ((line = stderr.readLine()) != null)
      ;
  }
}
