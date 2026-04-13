
import java.io.Serializable;

public class CompileFileTransaction implements Serializable {
  FileCarrier sourceFile;
  public CompileFileTransaction(String filename) throws Exception {
    sourceFile = new FileCarrier(filename);
  }
  public String getFilename() {
    return sourceFile.getFileName();
  }
}
