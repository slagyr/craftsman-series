package com.objectmentor.SMCRemote.transactions;

public class LoginTransaction implements SocketTransaction {
  public void accept(SocketTransactionProcessor processor) throws Exception {
    processor.process(this);
  }
}
