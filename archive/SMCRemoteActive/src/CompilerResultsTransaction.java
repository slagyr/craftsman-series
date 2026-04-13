
import java.io.Serializable;
import java.io.File;
import java.util.List;

public class CompilerResultsTransaction implements Serializable {
  private FileCarrier resultFile;

  public CompilerResultsTransaction() {
  }

  public CompilerResultsTransaction(File subdirectory, String file) throws Exception {
    resultFile = new FileCarrier(subdirectory, file);
  }

  public CompilerResultsTransaction(File subdirectory) throws Exception {
    String files[] = subdirectory.list();
    resultFile = new FileCarrier(subdirectory, files[0]);
  }

  public void write() throws Exception {
    resultFile.write();
  }
}
