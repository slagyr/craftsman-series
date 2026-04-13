package com.objectmentor.SMCRemote.transactions;

import com.objectmentor.SocketUtilities.*;

import java.io.File;

public class CompileFileTransaction implements SocketTransaction {
  private String[] args;
  private FileCarrier itsCarrier;

  public CompileFileTransaction(String[] args) {
    this.args = args;
    itsCarrier = new FileCarrier(null, args[0]);
  }

  public String[] getArgs() {
    return args;
  }

  public String getFileName() {
    return itsCarrier.getFilename();
  }

  public void write(File subDirectory) {
    itsCarrier.write(subDirectory);
  }

  public void accept(SocketTransactionProcessor processor) {
    processor.process(this);
  }
}
