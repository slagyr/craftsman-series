package com.objectmentor.SMCRemote.server;

import junit.framework.*;
import junit.swingui.TestRunner;

public class TestWholeServer extends TestSuite  {
  public TestWholeServer(String s) {
    super(s);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new TestSuite(com.objectmentor.SMCRemote.server.TestServerLogin.class));
    suite.addTest(new TestSuite(com.objectmentor.SMCRemote.server.TestCommandLine.class));
    suite.addTest(new TestSuite(com.objectmentor.SMCRemote.server.TestRegistration.class));
    suite.addTest(new TestSuite(com.objectmentor.SMCRemote.server.TestCompilation.class));
    suite.addTest(new TestSuite(com.objectmentor.SMCRemote.server.TestUserRepository.class));
    return suite;
  }
}
