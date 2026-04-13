package com.objectmentor.SMCRemote.server;

import junit.framework.TestCase;

import com.objectmentor.SMCRemote.transactions.*;

import java.io.*;
import java.net.Socket;

public class TestBase extends TestCase {
  protected ObjectInputStream is;
  protected ObjectOutputStream os;
  protected SMCRemoteService service;
  protected Socket client;

  protected UserDirectory mockUserDirectory = new UserDirectory() {
    public boolean isValid(String username, String password) {
      return true;
    }

    public String getPassword(String username) {
      return null;
    }

    public boolean add(String username, String password) {
      return true;
    }
  };

  protected UserDirectory mockUserInvalidator = new UserDirectory() {
    public boolean isValid(String username, String password) {
      return false;
    }

    public String getPassword(String username) {
      return null;
    }

    public boolean add(String username, String password) {
      return false;
    }
  };

  public TestBase(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    SMCRemoteService.isVerbose = false;
  }

  public void tearDown() throws Exception {
  }

  protected boolean login() throws IOException, ClassNotFoundException {
    LoginTransaction lt = new LoginTransaction("name", "password");
    sendToServer(lt);
    LoginResponseTransaction ltr = (LoginResponseTransaction) is.readObject();
    return ltr.isAccepted();
  }

  protected void sendToServer(SocketTransaction t) throws IOException {
    os.writeObject(t);
    os.flush();
  }

  protected void disconnectClientFromServer() throws Exception {
    Thread.sleep(500);
    client.close();
    service.close();
  }

  protected void connectClientToServer() throws Exception {
    service = new SMCRemoteService(999);
    client = new Socket("localhost", 999);
    is = new ObjectInputStream(client.getInputStream());
    os = new ObjectOutputStream(client.getOutputStream());

    String headerLine = (String) is.readObject();
    assert("headerline", headerLine.startsWith("SMCR Server"));
    assertEquals("discard message", null, is.readObject()); // discard message
  }
}

