package dtrack.messages;

public class SuitRegistrationAcceptanceMessage {
  public final String id;
  public final int argument;
  public final String sender;
  public final String recipient;

  public SuitRegistrationAcceptanceMessage(String id, int argument, String sender, String recipient) {
    this.id = id;
    this.argument = argument;
    this.sender = sender;
    this.recipient = recipient;
  }
}
