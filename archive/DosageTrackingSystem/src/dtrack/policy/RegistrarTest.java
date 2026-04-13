package dtrack.policy;

import junit.framework.TestCase;
import dtrack.messages.*;
import dtrack.gateways.*;
import dtrack.dto.Suit;

public class RegistrarTest extends TestCase {
  protected void setUp() throws Exception {
    SuitGateway.instance = new InMemorySuitGateway();
  }

  public void testAcceptRegistration() throws Exception {
    final String acceptId = "Suit Registration Accepted";
    SuitRegistrationAcceptanceMessage suitAck =
      new SuitRegistrationAcceptanceMessage(acceptId, 9999, "me", "you");
    Registrar.acceptMessageFromManufacturing(suitAck);
    Suit[] suits = (Suit[])SuitGateway.getArrayOfSuits();
    assertEquals(1, suits.length);
    assertEquals(9999, suits[0].barCode());
  }

  public void testRejectRegistration() throws Exception {
    final String rejectId = "Suit Registration Rejected";
    SuitRegistrationAcceptanceMessage suitNak =
      new SuitRegistrationAcceptanceMessage(rejectId, 9999, "me", "you");
    Registrar.acceptMessageFromManufacturing(suitNak);
    Suit[] suits = (Suit[])SuitGateway.getArrayOfSuits();
    assertEquals(0, suits.length);
  }
}
