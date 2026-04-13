package dtrack.fixtures;

import dtrack.Utilities;
import dtrack.messages.SuitRegistrationApprovalRequest;
import fit.ColumnFixture;

public class MessageSentToManufacturing extends ColumnFixture {
  private SuitRegistrationApprovalRequest message;
  public void execute() throws Exception {
    message = (SuitRegistrationApprovalRequest)Utilities.manufacturing.getLastMessage();
  }

  public boolean suitRegistrationApprovalRequest() {
    return message.id.equals(SuitRegistrationApprovalRequest.ID);
  }

  public String messageId() {
    return message.id;
  }

  public int messageArgument() {
    return message.argument;
  }

  public String messageSender() {
    return message.sender;
  }
}
