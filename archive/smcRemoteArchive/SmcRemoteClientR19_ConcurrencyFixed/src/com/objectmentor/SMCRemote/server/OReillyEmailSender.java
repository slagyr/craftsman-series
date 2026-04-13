package com.objectmentor.SMCRemote.server;

import com.oreilly.servlet.MailMessage;

import java.io.*;

public class OReillyEmailSender implements EmailSender {
  public boolean send(String emailAddress, String subject, String text) {
    try {
      MailMessage msg = new MailMessage("cvs.objectmentor.com");
      msg.from("info@objectmentor.com");
      msg.to(emailAddress);
      msg.setSubject(subject);
      PrintStream body = msg.getPrintStream();
      body.println(text);
      msg.sendAndClose();
      return true;
    } catch (IOException e) {
      System.err.println("Couldn't send email: " + e.getMessage());
      return false;
    }
  }
}
