
import junit.framework.TestCase;

import java.io.*;

public class FileCarrierTest extends TestCase {
  private abstract class FileComparator {
    abstract void writeFirstFile(PrintWriter w);
    abstract void writeSecondFile(PrintWriter w);

    void compare(boolean expected) throws Exception {
      File f1 = new File("f1");
      File f2 = new File("f2");
      PrintWriter w1 = new PrintWriter(new FileWriter(f1));
      PrintWriter w2 = new PrintWriter(new FileWriter(f2));
      writeFirstFile(w1);
      writeSecondFile(w2);
      w1.close();
      w2.close();
      assertEquals("(f1,f2)", expected, filesAreTheSame(f1, f2));
      assertEquals("(f2,f1)", expected, filesAreTheSame(f2, f1));
      f1.delete();
      f2.delete();
    }
  }

  public void testOneFileLongerThanTheOther() throws Exception {
    FileComparator c = new FileComparator() {
      void writeFirstFile(PrintWriter w) {
        w.println("hi there");
      }

      void writeSecondFile(PrintWriter w) {
        w.println("hi there you");
      }
    };
    c.compare(false);
  }

  public void testFilesAreDifferentInTheMiddle() throws Exception {
    FileComparator c = new FileComparator() {
      void writeFirstFile(PrintWriter w) {
        w.println("hi there");
      }

      void writeSecondFile(PrintWriter w) {
        w.println("hi their");
      }
    };
    c.compare(false);
  }

  public void testSecondLineDifferent() throws Exception {
    FileComparator c = new FileComparator() {
      void writeFirstFile(PrintWriter w) {
        w.println("hi there");
        w.println("This is fun");
      }

      void writeSecondFile(PrintWriter w) {
        w.println("hi there");
        w.println("This isn't fun");
      }
    };
    c.compare(false);
  }

  public void testFilesSame() throws Exception {
    FileComparator c = new FileComparator() {
      void writeFirstFile(PrintWriter w) {
        w.println("hi there");
      }

      void writeSecondFile(PrintWriter w) {
        w.println("hi there");
      }
    };
    c.compare(true);
  }

  public void testMultipleLinesSame() throws Exception {
    FileComparator c = new FileComparator() {
      void writeFirstFile(PrintWriter w) {
        w.println("hi there");
        w.println("this is fun");
        w.println("Lots of fun");
      }

      void writeSecondFile(PrintWriter w) {
        w.println("hi there");
        w.println("this is fun");
        w.println("Lots of fun");
      }
    };
    c.compare(true);
  }


  public void testFileCarrier() throws Exception {
    final String ORIGINAL_FILENAME = "testFileCarrier.txt";
    final String RENAMED_FILENAME = "testFileCarrierRenamed.txt";
    File originalFile = new File(ORIGINAL_FILENAME);
    File renamedOriginal = new File(RENAMED_FILENAME);

    ensureFileIsRemoved(originalFile);
    ensureFileIsRemoved(renamedOriginal);

    createTestFile(originalFile);
    FileCarrier fc = new FileCarrier(ORIGINAL_FILENAME);
    rename(originalFile, renamedOriginal);
    fc.write();

    assertTrue(originalFile.exists());
    assertTrue(filesAreTheSame(originalFile, renamedOriginal));

    originalFile.delete();
    renamedOriginal.delete();
  }

  private void rename(File oldFile, File newFile) {
    oldFile.renameTo(newFile);
    assertTrue(oldFile.exists() == false);
    assertTrue(newFile.exists());
  }

  private void createTestFile(File file) throws IOException {
    PrintWriter w = new PrintWriter(new FileWriter(file));
    w.println("line one");
    w.println("line two");
    w.println("line three");
    w.close();
  }

  private void ensureFileIsRemoved(File file) {
    if (file.exists()) file.delete();
    assertTrue(file.exists() == false);
  }

  private boolean filesAreTheSame(File f1, File f2) throws Exception {
    FileInputStream r1 = new FileInputStream(f1);
    FileInputStream r2 = new FileInputStream(f2);
    try {
      int c;
      while ((c = r1.read()) != -1) {
        if (r2.read() != c) {
          return false;
        }
      }
      if (r2.read() != -1)
        return false;
      else
        return true;
    } finally {
      r1.close();
      r2.close();
    }
  }
}
