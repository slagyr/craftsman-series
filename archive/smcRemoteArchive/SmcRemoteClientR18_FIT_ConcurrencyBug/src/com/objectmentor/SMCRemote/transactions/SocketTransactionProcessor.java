package com.objectmentor.SMCRemote.transactions;


public class SocketTransactionProcessor {
  public void process(CompileFileTransaction t) throws Exception {
    throw new NoProcessorException("CompileFileTransaction");
  }

  public void process(CompilerResultsTransaction t) throws Exception {
    throw new NoProcessorException("CompilerResultsTransaction");
  }

  public void process(LoginTransaction t) throws Exception {
    throw new NoProcessorException("LoginTransaction");
  }

  public void process(LoginResponseTransaction t) throws Exception {
    throw new NoProcessorException("LoginResponseTransaction");
  }

  public void process(RegistrationTransaction t) throws Exception {
    throw new NoProcessorException("RegistrationTransaction");
  }

  public void process(RegistrationResponseTransaction t) throws Exception {
    throw new NoProcessorException("RegistrationResponseTransaction");
  }

}
