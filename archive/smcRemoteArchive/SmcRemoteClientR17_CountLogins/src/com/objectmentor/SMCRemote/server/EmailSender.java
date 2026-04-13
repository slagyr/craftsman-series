package com.objectmentor.SMCRemote.server;

public interface EmailSender {
  public boolean send(String emailAddress, String subject, String text);
}
