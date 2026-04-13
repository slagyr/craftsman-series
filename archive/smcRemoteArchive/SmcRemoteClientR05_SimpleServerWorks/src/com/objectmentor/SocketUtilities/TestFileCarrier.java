package com.objectmentor.SocketUtilities;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

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

  private abstract class FileComparator {
    File f1;
    File f2;

    abstract void writeFirstFile(PrintWriter w);
    abstract void writeSecondFile(PrintWriter w);

    void compare(boolean expected) throws Exception {
    f1 = new File("f1");
    f2 = new File("f2");
    PrintWriter w1 = new PrintWriter(new FileWriter(f1));
    PrintWriter w2 = new PrintWriter(new FileWriter(f2));
    writeFirstFile(w1);
    writeSecondFile(w2);
    w1.close();
    w2.close();
    assertEquals("(f1,f2)", expected ,filesAreTheSame(f1,f2));
    assertEquals("(f2,f1)", expected, filesAreTheSame(f2,f1));
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
    File sourceFile = new File("testFileCarrier.txt");
    PrintWriter w = new PrintWriter(new FileWriter(sourceFile));
    w.println("line one");
    w.println("line two");
    w.println("line three");
    w.close();

    FileCarrier fc = new FileCarrier("testFileCarrier.txt");
    assertTrue(fc.isError() == false);
    assertTrue(fc.isLoaded() == true);

    File tmpDirectory = new File("tmpDirectory");
    tmpDirectory.mkdir();

    File newFile = new File("tmpDirectory/testFileCarrier.txt");

    fc.write(tmpDirectory);

    assertTrue("file wasn't written", newFile.exists());
    assertTrue("files aren't the same.", filesAreTheSame(newFile, sourceFile));

    assertTrue("newfile", newFile.delete());
    assertTrue("oldFile", sourceFile.delete());
    assertTrue("directory", tmpDirectory.delete());
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
