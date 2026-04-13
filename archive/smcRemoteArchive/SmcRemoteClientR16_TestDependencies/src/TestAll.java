import junit.framework.*;
import junit.swingui.TestRunner;

public class TestAll {
  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestAll"});
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("TestAll");
    suite.addTest(new TestSuite(com.objectmentor.SocketUtilities.TestFileCarrier.class));
    suite.addTest(com.objectmentor.SMCRemote.client.TestWholeClient.suite());
    suite.addTest(com.objectmentor.SMCRemote.server.TestWholeServer.suite());
    suite.addTest(new TestSuite(TestDependencies.class));
    return suite;
  }
}
