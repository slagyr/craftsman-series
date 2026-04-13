package com.objectmentor.SMCRemote.server;

public interface UserDirectory {
  public boolean isValid(String username, String password);
  public boolean add(String username, String password) throws Exception;
  public String getPassword(String username);
  public int incrementLoginCount(String username) throws Exception;
}
