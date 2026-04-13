package com.objectmentor.SMCRemote.transactions;

import com.objectmentor.SocketUtilities.SocketTransaction;
import com.objectmentor.SocketUtilities.FileCarrier;

public class CompileFileTransaction implements SocketTransaction {
  private String[] args;
  private FileCarrier itsCarrier;

  public CompileFileTransaction(String[] args) {
    this.args = args;
    itsCarrier = new FileCarrier(args[0]);
  }

  public String[] getArgs() {
    return args;
  }

  public String getFileName() {
    return itsCarrier.getFilename();
  }

  public void accept(SocketTransactionProcessor processor) {
    processor.process(this);
  }
}
