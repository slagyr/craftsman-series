package dtrack.policy;

import dtrack.Utilities;
import dtrack.dto.Suit;
import dtrack.gateways.SuitGateway;
import dtrack.messages.SuitRegistrationAcceptanceMessage;

public class Registrar {
  public static void acceptMessageFromManufacturing(Object message) {
    SuitRegistrationAcceptanceMessage suitAck = (SuitRegistrationAcceptanceMessage) message;
    if (suitAck.id.equals("Suit Registration Accepted")) {
      registerSuit(suitAck.argument);
    }
  }

  public static boolean registerSuit(int barcode) {
    Suit acceptedSuit = new Suit(barcode, Utilities.getDate());
    SuitGateway.add(acceptedSuit);
    return true;
  }

  public static boolean attemptToRegisterNewSuit(int barcode) {
    if (SuitGateway.isSuitRegistered(barcode))
      return false;
    return Utilities.manufacturing.requestApprovalForRegistration(barcode);
  }
}
