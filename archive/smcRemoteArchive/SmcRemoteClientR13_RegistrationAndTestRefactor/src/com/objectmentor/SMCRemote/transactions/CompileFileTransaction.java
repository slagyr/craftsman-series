package com.objectmentor.SMCRemote.transactions;

import com.objectmentor.SocketUtilities.FileCarrier;

import java.io.File;

public class CompileFileTransaction implements SocketTransaction {
  private FileCarrier itsCarrier;
  private String itsGenerator;

  public CompileFileTransaction(String filename, String generator) {
    itsCarrier = new FileCarrier(null, filename);
    itsGenerator = generator;
  }

  public String getFilename() {
    return itsCarrier.getFilename();
  }

  public String getGenerator() {
    return itsGenerator;
  }

  public void write(File subDirectory) {
    itsCarrier.write(subDirectory);
  }

  public void accept(SocketTransactionProcessor processor) throws Exception {
    processor.process(this);
  }
}
