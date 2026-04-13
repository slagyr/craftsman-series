package com.objectmentor.SMCRemote.server;

public class ServerControllerContext {
  public void FSMError(String event, String state) {
    System.out.println("Transition Error.  Event:" + event + " in state:" + state);
  }

  public void checkValidUser() {}
  public void close() {}
  public void acknowledgeLogin() {}
  public void rejectLogin() {}
  public void doCompile() {}
  public void sendCompileResults() {}
  public void sendCompileRejection() {}
  public void sendCompileError() {}
  public void reportError() {}
}
