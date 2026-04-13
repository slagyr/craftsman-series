
import java.io.*;
import java.net.Socket;

public class SMCRemoteClient {
  private String itsFilename;
  private ObjectInputStream is;
  private ObjectOutputStream os;
  private Socket smcrSocket;

  public static void main(String[] args) {
    SMCRemoteClient client = new SMCRemoteClient();
    client.setFilename(args[0]);
    if (!client.prepareFile()) {
      System.out.println("failed to prepare");
      return;
    }
    if (!client.connect()) {
      System.out.println("failed to connect");
      return;
    }
    if (!client.compileFile()) {
      System.out.println("failed to compile");
      client.close();
      return;
    }
    client.close();
  }

  public boolean parseCommandLine(String[] args) {
    try {
      itsFilename = args[0];
    } catch (ArrayIndexOutOfBoundsException e) {
      return false;
    }
    return true;
  }

  public String filename() {
    return itsFilename;
  }

  public void setFilename(String itsFilename) {
    this.itsFilename = itsFilename;
  }

  public boolean prepareFile() {
    boolean filePrepared = false;
    File f = new File(itsFilename);
    if (f.exists()) {
      filePrepared = true;
    }
    return filePrepared;
  }

  public boolean connect() {
    boolean connectionStatus = false;
    try {
      smcrSocket = new Socket("localhost", 9000);
      is = new ObjectInputStream(smcrSocket.getInputStream());
      os = new ObjectOutputStream(smcrSocket.getOutputStream());
      String headerLine = (String) is.readObject();
      connectionStatus = headerLine != null && headerLine.startsWith("SMCR");
    } catch (Exception e) {
      e.printStackTrace();
      connectionStatus = false;
    }
    return connectionStatus;
  }

  public void close() {
    try {
      if (is != null) is.close();
      if (os != null) os.close();
      if (smcrSocket != null) smcrSocket.close();
    } catch (IOException e) {
    }
  }

  public boolean compileFile() {
    boolean fileCompiled = false;
    try {
      CompileFileTransaction cft = new CompileFileTransaction(itsFilename);
      os.writeObject(cft);
      os.flush();
      Object response = is.readObject();
      CompilerResultsTransaction crt = (CompilerResultsTransaction) response;
      crt.write();
      fileCompiled = true;
    } catch (Exception e) {
      e.printStackTrace();
      fileCompiled = false;
    }
    return fileCompiled;
  }
}
