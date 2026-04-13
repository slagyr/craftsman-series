package com.objectmentor.SMCRemote.transactions;

public class RegistrationResponseTransaction implements SocketTransaction {
  private boolean confirmed;
  private String failureReason;

  public RegistrationResponseTransaction(boolean confirmed) {
    this.confirmed = confirmed;
  }

  public String getFailureReason() {
    return failureReason;
  }

  public void setFailureReason(String failureReason) {
    this.failureReason = failureReason;
  }

  public boolean isConfirmed() {
    return confirmed;
  }

  public void accept(SocketTransactionProcessor processor) throws Exception {
    processor.process(this);
  }
}
