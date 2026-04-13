package dtrack.gateways;

import dtrack.dto.Suit;

import java.util.*;

public class InMemorySuitGateway implements ISuitGateway {
  private Map<Integer, Suit> suits = new HashMap<Integer,Suit>();
  public void add(Suit suit) {
    suits.put(suit.barCode(), suit);
  }

  public int getNumberOfSuits() {
    return suits.size();
  }

  public Suit[] getArrayOfSuits() {
    return suits.values().toArray(new Suit[0]);
  }

  public boolean isSuitRegistered(int barcode) {
    return suits.containsKey(barcode);
  }
}
