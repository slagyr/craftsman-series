package dtrack.fixtures;

import dtrack.Utilities;
import dtrack.policy.Registrar;
import dtrack.dto.Suit;
import dtrack.gateways.SuitGateway;

public class DTrackFixture {
  public void setSuitAsRegistered(int barcode) {
    SuitGateway.add(new Suit(barcode, Utilities.getDate()));
  }

  public boolean registerSuit(int barcode) {
    return Registrar.attemptToRegisterNewSuit(barcode);
  }

  public boolean wasAMessageSentToManufacturing() {
    return Utilities.manufacturing.getLastMessage() != null;
  }

  public int countOfRegisteredSuitsIs() {
    return SuitGateway.getNumberOfSuits();
  }

  public boolean errorMessage(String message) {
    return false;
  }
}
