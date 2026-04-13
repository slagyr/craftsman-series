package com.objectmentor.SMCRemote.client;

import com.objectmentor.SMCRemote.transactions.*;

public class RemoteRegistrar extends RemoteSessionBase {

  public RemoteRegistrar(String itsHost, int itsPort, MessageLogger logger) {
    super(itsHost, itsPort, logger);
  }

  public void connectAndRegister(String registrant) {
    if (connect()) {
      RegistrationResponseTransaction rrt;
      if ((rrt = register(registrant)) != null) {
        if (rrt.isConfirmed()) {
          logMessage(registrant + " was registered");
          System.out.println("User: " + registrant + " registered.  Email sent.");
        } else {
          logMessage(registrant + " was NOT registered: " + rrt.getFailureReason());
          System.out.println(registrant + " was NOT registered: " + rrt.getFailureReason());
        }
      } else { // rrt == null
        System.out.println("Something bad happened.  Sorry.");
      }
      close();
    } else {  // conect
      System.out.println("failed to connect to " + getHost() + ":" + getPort());
    }

  }

  RegistrationResponseTransaction register(String registrant) {
    logMessage("Attempting to register " + registrant);
    RegistrationTransaction t = new RegistrationTransaction(registrant);
    sendTransaction(t);
    RegistrationResponseTransaction rrt = null;
    try {
      rrt = (RegistrationResponseTransaction) readServerObject();
    } catch (Exception e) {
      logMessage("Could not send registration response: " + e.getMessage());
      return null;
    }
    return rrt;
  }
}
