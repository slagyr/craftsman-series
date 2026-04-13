package com.objectmentor.SMCRemote.server;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

public class TestOReillyEmail extends TestCase {
  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestOReillyEmail"});
  }

  public TestOReillyEmail(String name) {
    super(name);
  }

  public void setUp() throws Exception {
  }

  public void tearDown() throws Exception {
  }

  public void testSendEmail() throws Exception {
    boolean emailStatus = false;
    OReillyEmailSender sender = new OReillyEmailSender();
    emailStatus = sender.send("unclebob@objectmentor.com", "hi bob", "oh boy, email!");
    assertEquals("SendEmail", true, emailStatus);
  }
}