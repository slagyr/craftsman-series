
import junit.framework.TestCase;
import junit.swingui.TestRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.objectmentor.SocketService.SocketService;
import com.objectmentor.SocketService.SocketServer;

public class TestSMCRemoteClient extends TestCase {

    private static final int SMCPORT = 9000;

    public static void main(String[] args) {
        TestRunner.main(new String[]{"TestSMCRemoteClient"});
    }

    public TestSMCRemoteClient(String name) {
        super(name);
    }

    private SMCRemoteClient c;

    public void setUp() throws Exception {
        c = new SMCRemoteClient();
    }

    public void tearDown() throws Exception {
    }

    public void testParseCommandLine() throws Exception {
        c.parseCommandLine(new String[]{"filename"});
        assertEquals("filename", c.filename());
    }

    public void testParseInvalidCommandLine() {
        boolean result = c.parseCommandLine(new String[0]);
        assertTrue("result should be false", !result);
    }

    public void testCountBytesInFile() throws Exception {
        File f = new File("testFile");
        FileOutputStream stream = new FileOutputStream(f);
        stream.write("some text".getBytes());
        stream.close();

        c.setFilename("testFile");
        boolean prepared = c.prepareFile();
        f.delete();
        assertTrue(prepared);
        assertEquals(9, c.getFileLength());
    }

    public void testFileDoesNotExist() throws Exception {
        c.setFilename("thisFileDoesNotExist");
        boolean prepared = c.prepareFile();
        assertEquals(false, prepared);
    }

    public void testConnectToSMCRemoteServer() throws Exception {
        SocketServer server = new SocketServer() {
            public void serve(Socket socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        };
        SocketService smc = new SocketService(SMCPORT, server);
        boolean connection = c.connect();
        assertTrue(connection);
    }
}