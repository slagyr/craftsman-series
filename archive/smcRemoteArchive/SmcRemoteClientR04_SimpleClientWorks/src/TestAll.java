
import junit.framework.*;
import junit.swingui.TestRunner;
import com.objectmentor.SMCRemote.client.TestSMCRemoteClient;

public class TestAll {
  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestAll"});
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("TestAll");
    suite.addTest(new TestSuite(TestSMCRemoteClient.class));
    suite.addTest(new TestSuite(com.objectmentor.SocketUtilities.TestFileCarrier.class));
    return suite;
  }
}
