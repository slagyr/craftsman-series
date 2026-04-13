package SMCRUtilities;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import java.io.*;

public class TestFileCarrier extends TestCase {
  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestFileCarrier"});
  }

  public TestFileCarrier(String name) {
    super(name);
  }

  public void setUp() throws Exception {
  }

  public void tearDown() throws Exception {
  }


  public void testCreateFileCarrier() throws Exception {
    File f = new File("testFileCarrier.txt");
    File renamedOriginal = new File("testFileCarrierRenamed.txt");
    if (f.exists()) f.delete();
    if (renamedOriginal.exists()) renamedOriginal.delete();
    assertTrue(f.exists() == false);
    assertTrue(renamedOriginal.exists() == false);

    PrintWriter w = new PrintWriter(new FileWriter(f));
    w.println("line one");
    w.println("line two");
    w.println("line three");
    w.close();

    FileCarrier fc = new FileCarrier("testFileCarrier.txt");
    assertTrue(fc.isError() == false);
    assertTrue(fc.isLoaded() == true);

    f.renameTo(renamedOriginal);
    assertTrue("file wasn't renamed", f.exists() == false);

    fc.write();
    assertTrue("file wasn't written", f.exists());
    assertTrue("files aren't the same.", filesAreTheSame(f, renamedOriginal));
  }

  boolean filesAreTheSame(File f1, File f2) throws Exception {
    FileInputStream r1 = new FileInputStream(f1);
    FileInputStream r2 = new FileInputStream(f2);
    try {
      int c;
      while ((c = r1.read()) != -1) {
        if (r2.read() != c) {
          return false;
        }
      }
      if (r1.read() != -1)
        return false;
      else
        return true;
    } finally {
      r1.close();
      r2.close();
    }
  }
}
