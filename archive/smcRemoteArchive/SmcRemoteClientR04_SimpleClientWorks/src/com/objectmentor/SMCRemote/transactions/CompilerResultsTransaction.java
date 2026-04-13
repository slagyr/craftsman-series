package com.objectmentor.SMCRemote.transactions;

import com.objectmentor.SocketUtilities.FileCarrier;
import com.objectmentor.SocketUtilities.SocketTransaction;

public class CompilerResultsTransaction implements SocketTransaction {
  private FileCarrier[] files;
  private String[] filenames;

  public String[] getFilenames() {
    return filenames;
  }

  public CompilerResultsTransaction(String[] filenames) {
    this.filenames = filenames;
    files = new FileCarrier[filenames.length];
    for (int fileIndex = 0; fileIndex < filenames.length; fileIndex++) {
      files[fileIndex] = new FileCarrier(filenames[fileIndex]);
    }
  }

  public void write() {
    for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
      FileCarrier carrier = files[fileIndex];
      carrier.write();
    }
  }

  public void accept(SocketTransactionProcessor processor) {
    processor.process(this);
  }
}
