package dtrack.fixtures;

import dtrack.messages.SuitRegistrationAcceptanceMessage;
import dtrack.policy.Registrar;
import fit.ColumnFixture;

public class MessageReceivedFromManufacturing extends ColumnFixture {
  public String messageId;
  public int messageArgument;
  public String messageSender;
  public String messageRecipient;
  public void execute() {
    SuitRegistrationAcceptanceMessage message =
      new SuitRegistrationAcceptanceMessage(messageId,
                                   messageArgument,
                                   messageSender,
                                   messageRecipient);
    Registrar.acceptMessageFromManufacturing(message);
  }
}
