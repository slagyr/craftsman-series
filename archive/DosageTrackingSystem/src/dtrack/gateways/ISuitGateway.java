package dtrack.gateways;

import dtrack.dto.Suit;

public interface ISuitGateway {
  public void add(Suit suit);
  int getNumberOfSuits();
  Suit[] getArrayOfSuits();
  boolean isSuitRegistered(int barcode);
}
