package com.objectmentor.SMCRemote.server;

import com.objectmentor.SMCRemote.transactions.*;
import com.objectmentor.SocketService.*;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class SMCRemoteService {
  private SocketService service;

  public static final String COMPILE_COMMAND = "java -cp c:\\SMC\\smc.jar smc.Smc -f";

  public SMCRemoteService(int port) throws Exception {
    service = new SocketService(port, new SMCRemoteServer());
  }

  public void close() throws Exception {
    service.close();
  }

  public static void main(String[] args) {
    try {
      SMCRemoteService service = new SMCRemoteService(9000);
    } catch (Exception e) {
      System.err.println("Could not connect");
    }
  }

  static String buildCommand(String[] args) {
    StringBuffer command = new StringBuffer(COMPILE_COMMAND);
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

  static File makeTempDirectory() {
    File tmpDirectory;
    do {
      long millis = System.currentTimeMillis();
      tmpDirectory = new File("smcTempDirectory" + millis);
    } while (tmpDirectory.exists());
    tmpDirectory.mkdir();
    return tmpDirectory;
  }
}

class SMCRemoteServer implements SocketServer {
  private ObjectOutputStream serverOutput;
  private ObjectInputStream serverInput;
  private CompileFileTransaction cft;
  private File tempDirectory;
  private CompilerResultsTransaction crt;

  public void serve(Socket socket) {
    try {
      initializeServer(socket);
      cft = (CompileFileTransaction) serverInput.readObject();

      tempDirectory = SMCRemoteService.makeTempDirectory();
      cft.write(tempDirectory);

      compile();
      sendCompilerResult();

      tempDirectory.delete();
      socket.close();
    } catch (Exception e) {
    }
  }

  private void compile() throws Exception {
    String args[] = cft.getArgs();
    crt = new CompilerResultsTransaction();
    File batFile = writeCompileScript();

    SMCRemoteService.executeCommand(tempDirectory + "\\smc.bat", crt.getStdoutLines(), crt.getStderrLines());

    batFile.delete();
    File sourceFile = new File(tempDirectory, args[0]);
    sourceFile.delete();
  }

  private File writeCompileScript() throws IOException {
    File batFile = new File(tempDirectory, "smc.bat");
    PrintWriter bat = new PrintWriter(new FileWriter(batFile));
    bat.println("cd " + tempDirectory);
    bat.println(SMCRemoteService.buildCommand(cft.getArgs()));
    bat.close();
    return batFile;
  }

  private void sendCompilerResult() throws IOException {
    String[] filenames = tempDirectory.list();
    crt.loadFiles(tempDirectory, filenames);
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
    serverOutput.writeObject("SMCR Server.  $Id: SMCRemoteService.java,v 1.3 2002/09/29 21:23:53 Administrator Exp $");
    serverOutput.flush();
  }

}
