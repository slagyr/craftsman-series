package com.objectmentor.SMCRemote.transactions;

public class LoginResponseTransaction implements SocketTransaction {
  private boolean isAccepted;

  public LoginResponseTransaction(boolean accepted) {
    isAccepted = accepted;
  }

  public boolean isAccepted() {
    return isAccepted;
  }

  public void accept(SocketTransactionProcessor processor) throws Exception {
    processor.process(this);
  }
}
