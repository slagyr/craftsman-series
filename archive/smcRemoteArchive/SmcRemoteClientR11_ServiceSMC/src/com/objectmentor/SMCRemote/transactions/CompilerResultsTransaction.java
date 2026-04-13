package com.objectmentor.SMCRemote.transactions;

import com.objectmentor.SocketUtilities.FileCarrier;

import java.io.File;
import java.util.Vector;

public class CompilerResultsTransaction implements SocketTransaction {
  public static final int OK = 0;
  public static final int NOT_LOGGED_IN = 1;

  private FileCarrier[] files;
  private String[] filenames;
  private Vector stdout;
  private Vector stderr;
  private int status;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Vector getStdoutLines() {
    return stdout;
  }

  public Vector getStderrLines() {
    return stderr;
  }

  public String[] getFilenames() {
    return filenames;
  }

  public CompilerResultsTransaction() {
    stdout = new Vector();
    stderr = new Vector();
  }

  public void loadFiles(File subDirectory, String[] filenames) {
    this.filenames = filenames;
    files = new FileCarrier[filenames.length];
    for (int fileIndex = 0; fileIndex < filenames.length; fileIndex++) {
      files[fileIndex] = new FileCarrier(subDirectory, filenames[fileIndex]);
    }
  }

  public void write() {
    for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
      FileCarrier carrier = files[fileIndex];
      carrier.write();
    }
  }

  public void accept(SocketTransactionProcessor processor) throws Exception {
    processor.process(this);
  }
}
