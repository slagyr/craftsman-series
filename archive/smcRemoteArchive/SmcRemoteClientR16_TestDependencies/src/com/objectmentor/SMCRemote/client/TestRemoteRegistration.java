package com.objectmentor.SMCRemote.client;

import junit.swingui.TestRunner;

import com.objectmentor.SMCRemote.transactions.*;
import com.objectmentor.SocketService.SocketService;


class MockRemoteRegistrationServer extends MockServerBase {
  public String user;

  class RemoteRegistrationServerTransactionProcessor extends SocketTransactionProcessor {
    public void process(RegistrationTransaction t) throws Exception {
      user = t.getUsername();
      RegistrationResponseTransaction rrt;
      if (user.equals("goodUser")) {
        rrt = new RegistrationResponseTransaction(true);
      } else {
        rrt = new RegistrationResponseTransaction(false);
      }
      sendTransaction(rrt);
    }
  }

  public SocketTransactionProcessor getProcessor() {
    return new RemoteRegistrationServerTransactionProcessor();
  }
}

public class TestRemoteRegistration extends TestClientBase {
  public TestRemoteRegistration(String s) {
    super(s);
  }

  public static void main(String[] args) {
    TestRunner.main(new String[]{"com.objectmentor.SMCRemote.client.TestRemoteRegistration"});
  }

  private RemoteRegistrar r;
  private MockRemoteRegistrationServer server;
  private SocketService smc;

  public void setUp() throws Exception {
    super.setUp();
    r = new RemoteRegistrar("localhost", SMCPORT, new NullMessageLogger());
    server = new MockRemoteRegistrationServer();
    smc = new SocketService(SMCPORT, server);
  }

  public void tearDown() throws Exception {
    r.close();
    smc.close();
  }

  public void testRegistration() throws Exception {
    RegistrationResponseTransaction rrt;
    assert(r.connect());
    rrt = r.register("goodUser");
    assert("rrt not null", rrt != null);
    assert("registration passed", rrt.isConfirmed());
    Thread.sleep(50);
    assertEquals("Registration", "goodUser", server.user);
  }

  public void testRegistrationMain() throws Exception {
    runMain(new String[]{"-r", "goodUser"});
    assert("Registration message", getStdout().startsWith("User: goodUser registered.  Email sent."));
  }
}
