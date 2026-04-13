package com.objectmentor.SMCRemote.server;

public interface UserValidator {
  public boolean isValid(String username, String password);
}
