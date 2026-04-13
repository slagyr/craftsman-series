package com.objectmentor.SMCRemote.client;

public interface ClientCommandLineProcessor {
  public void setGenericParameters(String host, int port, boolean verbose);
  public void compile(String username, String password, String generator, String filename);
  public void register(String registrant);
}
