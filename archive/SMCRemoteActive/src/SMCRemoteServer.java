
import com.objectmentor.SocketService.*;

import java.io.*;
import java.net.Socket;

public class SMCRemoteServer {
  private static final String SMC_CLASSPATH = "C:/SMC/smc.jar";
  private static final String SMC_CLASSNAME = "smc.Smc";
  private static int workingDirectoryIndex = 0;

  public SMCRemoteServer(int port) throws Exception {
    SocketService service = new SocketService(port, new SMCRemoteServerThread());
  }

  public static String buildCommandLine(String file) {
    return "java -cp " +
           SMC_CLASSPATH + " " +
           SMC_CLASSNAME +
           " -f " + file;
  }

  public static boolean executeCommand(String command) {
    Runtime rt = Runtime.getRuntime();
    try {
      Process p = rt.exec("sh -c \"" + command + "\"");
      p.waitFor();
      return p.exitValue() == 0;
    } catch (Exception e) {
      return false;
    }
  }

  public static File makeWorkingDirectory() {
    File workingDirectory = new File(makeUniqueWorkingDirectoryName());
    workingDirectory.mkdir();
    return workingDirectory;
  }

  private static String makeUniqueWorkingDirectoryName() {
    return "workingDirectory" +
           System.currentTimeMillis() + "_" +
           workingDirectoryIndex++;
  }

  public static void deleteWorkingDirectory(File workingDirectory) {
    File[] files = workingDirectory.listFiles();
    for (int i = 0; i < files.length; i++) {
      if (files[i].isDirectory())
        deleteWorkingDirectory(files[i]);
      files[i].delete();
    }
    workingDirectory.delete();
  }

  public static CompilerResultsTransaction
    compile(CompileFileTransaction cft, String command) throws Exception {
    File workingDirectory = makeWorkingDirectory();
    String wd = workingDirectory.getName();
    cft.sourceFile.write(workingDirectory);
    executeCommand("cd " + wd + ";" + command);
    new File(workingDirectory, cft.getFilename()).delete();
    CompilerResultsTransaction crt = new CompilerResultsTransaction(workingDirectory);
    deleteWorkingDirectory(workingDirectory);
    return crt;
  }

  private static class SMCRemoteServerThread implements SocketServer {
    private ObjectOutputStream os;
    private ObjectInputStream is;
    private CompileFileTransaction cft;
    private CompilerResultsTransaction crt;

    public void serve(Socket theSocket) {
      try {
        initializeStreams(theSocket);
        sayHello();
        readTransaction();
        doCompile();
        writeResponse();
        theSocket.close();
      } catch (Exception e) {
      }
    }

    private void readTransaction() throws IOException, ClassNotFoundException {
      cft = (CompileFileTransaction) is.readObject();
    }

    private void initializeStreams(Socket theSocket) throws IOException {
      os = new ObjectOutputStream(theSocket.getOutputStream());
      is = new ObjectInputStream(theSocket.getInputStream());
    }

    private void writeResponse() throws IOException {
      os.writeObject(crt);
      os.flush();
    }

    private void sayHello() throws IOException {
      os.writeObject("SMCR");
      os.flush();
    }

    private void doCompile() throws Exception {
      String command = buildCommandLine(cft.getFilename());
      crt = compile(cft, command);
    }
  }
}
