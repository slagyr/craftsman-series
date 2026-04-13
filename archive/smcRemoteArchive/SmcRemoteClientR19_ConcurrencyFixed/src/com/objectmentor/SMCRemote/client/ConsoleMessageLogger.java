package com.objectmentor.SMCRemote.client;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleMessageLogger implements MessageLogger {
  public void logMessage(String msg) {
    Date logTime = new Date();
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
    String logTimeString = fmt.format(logTime);
    System.out.println(logTimeString + " | " + msg);
  }
}
