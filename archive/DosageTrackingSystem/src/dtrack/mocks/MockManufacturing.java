package dtrack.mocks;

import dtrack.external.Manufacturing;
import dtrack.messages.SuitRegistrationApprovalRequest;

public class MockManufacturing extends Manufacturing {
  private Object lastMessage;

  protected void send(SuitRegistrationApprovalRequest msg) {
    lastMessage = msg;
  }

  public Object getLastMessage() {
    return lastMessage;
  }
}
