package com.objectmentor.SMCRemote.transactions;

public class LoginResponseTransaction implements SocketTransaction {
  private boolean isAccepted;
  private int loginCount;

  public LoginResponseTransaction(boolean accepted, int loginCount) {
    this.loginCount = loginCount;
    this.isAccepted = accepted;
  }

  public boolean isAccepted() {
    return isAccepted;
  }

  public int getLoginCount() {
    return loginCount;
  }

  public void accept(SocketTransactionProcessor processor) throws Exception {
    processor.process(this);
  }
}
