package com.objectmentor.SocketUtilities;

import java.io.*;
import java.util.*;

public class FileCarrier implements Serializable {
  private String itsFilename;
  private LinkedList itsLines = new LinkedList();
  private boolean loaded = false;
  private boolean error = false;

  public FileCarrier(File subDirectory, String filename) {
    File inputFile = new File(subDirectory, filename);
    itsFilename = new String(filename);
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
      String line;
      while ((line = br.readLine()) != null) {
        itsLines.add(line);
      }
      br.close();
      loaded = true;
    } catch (Exception e) {
      error = true;
    }
  }

  public void write() {
    write(null);
  }

  public void write(File subDirectory) {
    File f = new File(subDirectory, itsFilename);
    if (f.exists()) f.delete();
    try {
      PrintStream w = new PrintStream(new FileOutputStream(f));
      for (Iterator i = itsLines.iterator(); i.hasNext();) {
        String line = (String) i.next();
        w.println(line);
      }
      w.close();
    } catch (IOException e) {
      error = true;
    }
  }

  public boolean isLoaded() {
    return loaded;
  }

  public boolean isError() {
    return error;
  }

  public String getFilename() {
    return itsFilename;
  }
}
