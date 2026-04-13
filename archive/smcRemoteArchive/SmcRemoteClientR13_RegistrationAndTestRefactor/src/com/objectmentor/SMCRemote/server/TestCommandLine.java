package com.objectmentor.SMCRemote.server;

public class TestCommandLine extends TestBase {
  public TestCommandLine(String name) {
    super(name);
  }

  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void setUp() throws Exception {
    super.setUp();
  }

  public void testValidCommandLine() throws Exception {
    assert("Null Command Line", SMCRemoteService.parseCommandLine(new String[0]));
    assertEquals("default port", Integer.parseInt(SMCRemoteService.DEFAULT_PORT), SMCRemoteService.servicePort);
    assert("default verbose", SMCRemoteService.isVerbose == false);

    assert("Parametric Command Line", SMCRemoteService.parseCommandLine(new String[]{"-p", "999", "-v"}));
    assertEquals("port", 999, SMCRemoteService.servicePort);
    assert("verbose", SMCRemoteService.isVerbose == true);
  }

  public void testInvalidCommandLine() throws Exception {
    assertEquals("Invalid Command Line", false, SMCRemoteService.parseCommandLine(new String[]{"-x"}));
    assertEquals("Bad Port", false, SMCRemoteService.parseCommandLine(new String[]{"-p", "badport"}));
  }
}
