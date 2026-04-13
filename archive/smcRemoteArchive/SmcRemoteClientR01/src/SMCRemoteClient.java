import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class SMCRemoteClient {

  private String itsFilename = null;
  private long itsFileLength = 0;

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
    FileInputStream is = null;
    File f = new File(itsFilename);
    if (f.exists()) {
      itsFileLength = f.length();
      return true;
    } else
      return false;
  }

  public long getFileLength() {
    return itsFileLength;
  }

  public boolean connect() {
    try {
      Socket s = new Socket("localhost", 9000);
      return true;
    } catch (IOException e) {
    }
    return false;
  }
}
