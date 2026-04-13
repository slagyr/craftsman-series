import junit.framework.TestCase;
import junit.swingui.TestRunner;

import jdepend.framework.*;

import java.io.IOException;
import java.util.*;

public class TestDependencies extends TestCase {
  private JDepend jdepend;
  private Collection packages;

  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestDependencies"});
  }

  public TestDependencies(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    try {
      jdepend = new JDepend();
      jdepend.addDirectory("src");
      packages = jdepend.analyze();
    } catch (IOException e) {
      fail("JDepend Failure: " + e.getMessage());
    }
  }

  public void tearDown() throws Exception {
  }

  public void testDependencies() throws Exception {
    assertEquals("Cycles", false, jdepend.containsCycles());
    int clientCa = jdepend.getPackage("com.objectmentor.SMCRemote.client").afferentCoupling();
    int serverCa = jdepend.getPackage("com.objectmentor.SMCRemote.server").afferentCoupling();
    int transactionsCe = jdepend.getPackage("com.objectmentor.SMCRemote.transactions").efferentCoupling();
    assertEquals("client has incomming dependencies", 0, clientCa);
    assertEquals("server has incomming dependencies", 0, serverCa);
    assertEquals("transactions has outgoing dependencies", 0, transactionsCe);
  }
}