package com.objectmentor.SMCRemote.client;

import junit.framework.TestCase;

import com.objectmentor.SocketService.SocketServer;
import com.objectmentor.SMCRemote.transactions.*;

import java.io.*;
import java.net.Socket;

abstract class MockServerBase  implements SocketServer {
  private ObjectOutputStream os;
  private ObjectInputStream is;

  public abstract SocketTransactionProcessor getProcessor();

  public void sendTransaction(SocketTransaction t) throws Exception {
    os.writeObject(t);
    os.flush();
  }

  public void serve(Socket socket) {
    try {
      os = new ObjectOutputStream(socket.getOutputStream());
      is = new ObjectInputStream(socket.getInputStream());
      os.writeObject("SMCR Test Server");
      os.writeObject(null);
      os.flush();
      while (true) {
        SocketTransaction t = (SocketTransaction) is.readObject();
        t.accept(getProcessor());
      }
    } catch (Exception e) {
    }
  }
}


public class TestClientBase extends TestCase {
  public static final int SMCPORT = 9000;

  private ByteArrayOutputStream stdoutBuffer;
  private ByteArrayOutputStream stderrBuffer;

  public TestClientBase(String s) {
    super(s);
  }

  protected void setUp() throws Exception {
    stdoutBuffer = new ByteArrayOutputStream();
    stderrBuffer = new ByteArrayOutputStream();
  }

  protected String getStderr() {
    return stderrBuffer.toString();
  }

  protected String getStdout() {
    return stdoutBuffer.toString();
  }

  protected void runMain(String[] args) throws IOException {
    PrintStream sysout = System.out;
    PrintStream syserr = System.err;

    System.setOut(new PrintStream(stdoutBuffer));
    System.setErr(new PrintStream(stderrBuffer));

    SMCRemoteClient.main(args);
    System.setOut(sysout);
    System.setErr(syserr);

    stdoutBuffer.close();
    stderrBuffer.close();

    Thread.yield();
  }
}
