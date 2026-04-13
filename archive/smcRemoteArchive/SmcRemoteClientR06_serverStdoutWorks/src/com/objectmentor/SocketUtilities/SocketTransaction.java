package com.objectmentor.SocketUtilities;

import com.objectmentor.SMCRemote.transactions.SocketTransactionProcessor;

import java.io.Serializable;

public interface SocketTransaction extends Serializable {
  public void accept(SocketTransactionProcessor processor);
}
