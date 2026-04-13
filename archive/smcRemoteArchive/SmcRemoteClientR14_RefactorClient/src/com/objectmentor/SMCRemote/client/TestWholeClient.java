package com.objectmentor.SMCRemote.client;

import junit.framework.*;

public class TestWholeClient extends TestSuite  {
  public TestWholeClient(String s) {
    super(s);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest(new TestSuite(com.objectmentor.SMCRemote.client.TestRemoteCompiler.class));
    suite.addTest(new TestSuite(com.objectmentor.SMCRemote.client.TestClientCommandLine.class));
    return suite;
  }
}
