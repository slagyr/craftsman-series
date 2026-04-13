package com.objectmentor.SMCRemote.server;


public class TestServerLogin extends TestBase {
  public TestServerLogin(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    super.setUp();
  }

  public void tearDown() throws Exception {
    super.tearDown();
  }

  public void testAcceptedLoginTransaction() throws Exception {
    boolean loggedIn = false;
    try {
      connectClientToServer();
      service.setUserDirectory(mockUserDirectory);
      loggedIn = login();
      disconnectClientFromServer();
    } catch (Exception e) {
    }

    assertEquals("LoginTransaction", true, loggedIn);
  }

  public void testRejectedLoginTransaction() throws Exception {
    boolean loggedIn = false;
    try {
      connectClientToServer();
      service.setUserDirectory(mockUserInvalidator);
      loggedIn = login();
      disconnectClientFromServer();
    } catch (Exception e) {
    }

    assertEquals("LoginTransaction", false, loggedIn);
  }

}
