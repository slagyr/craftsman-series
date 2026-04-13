package com.objectmentor.SMCRemote.transactions;

import java.io.Serializable;

public interface SocketTransaction extends Serializable {
  public void accept(SocketTransactionProcessor processor) throws Exception;
}
