package com.objectmentor.SMCRemote.transactions;

public class LoginTransaction implements SocketTransaction {
  private String itsUserName;
  private String itsPassword;

  public LoginTransaction(String itsUserName, String itsPassword) {
    this.itsUserName = itsUserName;
    this.itsPassword = itsPassword;
  }

  public String getUserName() {
    return itsUserName;
  }

  public String getPassword() {
    return itsPassword;
  }

  public void accept(SocketTransactionProcessor processor) throws Exception {
    processor.process(this);
  }
}
