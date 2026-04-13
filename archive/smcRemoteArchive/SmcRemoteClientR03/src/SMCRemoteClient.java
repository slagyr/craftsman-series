import java.io.*;
import java.net.Socket;

public class SMCRemoteClient {

  private String itsFilename = null;
  private long itsFileLength = 0;
  private Socket smcrSocket;
  private ObjectInputStream is;
  private ObjectOutputStream os;
  private BufferedReader fileReader;

  public boolean parseCommandLine(String[] args) {
    try {
      itsFilename = args[0];
    } catch (Exception e) {
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
    FileInputStream is = null;
    File f = new File(itsFilename);
    if (f.exists()) {
      try {
        itsFileLength = f.length();
        fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        filePrepared = true;
      } catch (FileNotFoundException e) {
        filePrepared = false;
      }
    }
    return filePrepared;
  }

  public long getFileLength() {
    return itsFileLength;
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

  public boolean sendFile() {
    boolean fileSent = false;
    try {
      writeSendFileCommand();
      fileSent = true;
    } catch (Exception e) {
      fileSent = false;
    }
    return fileSent;
  }

  private void writeSendFileCommand() throws IOException {
    os.writeObject("Sending");
    os.writeObject(itsFilename);
    os.writeLong(itsFileLength);
    char buffer[] = new char[(int) itsFileLength];
    fileReader.read(buffer);
    os.writeObject(buffer);
    os.flush();
  }

  public boolean execute(String[] command) {
    boolean executed = false;
    try {
      writeExecuteCommand(command);
      executed = true;
    } catch (IOException e) {
      executed = false;
    }
    return executed;
  }

  private void writeExecuteCommand(String[] command) throws IOException {
    os.writeObject("Execute");
    os.writeObject(command);
    os.flush();
  }
}
