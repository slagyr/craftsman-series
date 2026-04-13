package dtrack.messages;

public class SuitRegistrationApprovalRequest {
  public String id;
  public int argument;
  public String sender;
  public static final String ID = SuitRegistrationApprovalRequest.class.getName();

  public SuitRegistrationApprovalRequest() {
    id = ID;
  }
}
