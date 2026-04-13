
import java.io.*;
import java.util.*;

public class FileCarrier implements Serializable {
  private String fileName;
  private LinkedList lines = new LinkedList();
  private File subdirectory;

  public FileCarrier(String fileName) throws Exception {
    this(null, fileName);
  }

  public FileCarrier(File subdirectory, String fileName) throws Exception {
    this.fileName = fileName;
    this.subdirectory = subdirectory;
    loadLines();
  }

  private void loadLines() throws IOException {
    BufferedReader br = makeBufferedReader();
    String line;
    while ((line = br.readLine()) != null)
      lines.add(line);
    br.close();
  }

  private BufferedReader makeBufferedReader()
    throws FileNotFoundException {
    return new BufferedReader(
      new InputStreamReader(
        new FileInputStream(new File(subdirectory, fileName))));
  }

  public void write() throws Exception {
    write(null);
  }

  public void write(File subdirectory) throws Exception {
    PrintStream ps = makePrintStream(subdirectory);
    for (Iterator i = lines.iterator(); i.hasNext();)
      ps.println((String)i.next());
    ps.close();
  }

  private PrintStream makePrintStream(File subdirectory) throws FileNotFoundException {
    return new PrintStream(
      new FileOutputStream(new File(subdirectory,fileName)));
  }

  public String getFileName() {
    return fileName;
  }
}
