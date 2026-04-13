package com.objectmentor.SMCRemote.server;

import com.objectmentor.SMCRemote.transactions.*;
import com.objectmentor.SocketService.SocketServer;

import java.io.ObjectInputStream;
import java.net.Socket;

class SMCRemoteServer extends SocketTransactionProcessor {
  private ObjectInputStream serverInput;
  private boolean isOpen = false;
  private ServerSession session;

  public void serve(Socket socket) {
    isOpen = true;

    try {
      session = new ServerSession(this, socket);
      session.verboseMessage("Connected");
      serverInput = session.initializeSession(socket);
      while (isOpen) {
        SocketTransaction st = (SocketTransaction) serverInput.readObject();
        st.accept(this);
      }
    } catch (Exception e) {
      SMCRemoteService.verboseMessage("Connection torn down:" + e);
      return;
    }
    SMCRemoteService.verboseMessage("Connection closed normally.");
  }

  public void process(CompileFileTransaction t) throws Exception {
    session.compileEvent(t);
  }

  public void process(LoginTransaction t) throws Exception {
    session.loginEvent(t);
  }

  public void process(RegistrationTransaction t) throws Exception {
    session.registerEvent(t);
  }

  public void close() {
    isOpen = false;
  }
}
