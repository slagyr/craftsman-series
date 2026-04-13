package com.objectmentor.SMCRemote.transactions;


public interface SocketTransactionProcessor {
  public void process(CompileFileTransaction t);

  public void process(CompilerResultsTransaction t);
}
