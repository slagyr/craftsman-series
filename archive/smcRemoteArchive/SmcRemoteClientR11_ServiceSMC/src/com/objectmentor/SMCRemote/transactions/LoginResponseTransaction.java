package com.objectmentor.SMCRemote.transactions;

public class LoginResponseTransaction implements SocketTransaction {
  public void accept(SocketTransactionProcessor processor) throws Exception {
    processor.process(this);
  }
}
