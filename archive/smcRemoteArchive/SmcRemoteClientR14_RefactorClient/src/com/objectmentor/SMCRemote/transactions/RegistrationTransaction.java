package com.objectmentor.SMCRemote.transactions;

public class RegistrationTransaction  implements SocketTransaction {
  private String username;

  public String getUsername() {
    return username;
  }

  public RegistrationTransaction(String username) {
    this.username = username;
  }

  public void accept(SocketTransactionProcessor processor) throws Exception {
    processor.process(this);
  }
}
