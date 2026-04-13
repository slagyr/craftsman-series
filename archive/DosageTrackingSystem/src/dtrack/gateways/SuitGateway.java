package dtrack.gateways;

import dtrack.dto.Suit;

public class SuitGateway {
  public static ISuitGateway instance = null;

  public static int getNumberOfSuits() {
    return instance.getNumberOfSuits();
  }

  public static Object[] getArrayOfSuits() {
    return instance.getArrayOfSuits();
  }

  public static void add(Suit suit) {
    instance.add(suit);
  }

  public static boolean isSuitRegistered(int barcode) {
    return instance.isSuitRegistered(barcode);
  }
}
