package dtrack.external;

import dtrack.messages.SuitRegistrationApprovalRequest;

public abstract class Manufacturing {
  public boolean requestApprovalForRegistration(int barCode) {
    SuitRegistrationApprovalRequest msg = new SuitRegistrationApprovalRequest();
    msg.sender = "Outside Maintenance";
    msg.argument = barCode;
    send(msg);
    return true;
  }

  protected abstract void send(SuitRegistrationApprovalRequest msg);
}
