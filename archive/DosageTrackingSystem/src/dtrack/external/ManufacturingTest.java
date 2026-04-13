package dtrack.external;

import dtrack.messages.SuitRegistrationApprovalRequest;
import dtrack.mocks.MockManufacturing;
import junit.framework.TestCase;

public class ManufacturingTest extends TestCase {
  public void testRegisterSuitSendsMessageToMfg() throws Exception {
    MockManufacturing mfg = new MockManufacturing();
    mfg.requestApprovalForRegistration(7734);
    SuitRegistrationApprovalRequest message = (SuitRegistrationApprovalRequest) mfg.getLastMessage();
    assertEquals(SuitRegistrationApprovalRequest.ID, message.id);
    assertEquals("Outside Maintenance", message.sender);
    assertEquals(7734, message.argument);
  }
}
