package junk;

import dtrack.junk.SuitInventory;
import dtrack.dto.Suit;

import java.util.*;

public class SuitInspector {
  private SuitInventory inventory = new SuitInventory();

  public void inspectSuits() {
    List<Suit> overdueSuits = new ArrayList<Suit>();
    inventory.getSuitsOverdueForInspection(overdueSuits);
    for (Suit overdueSuit : overdueSuits) {
      overdueSuit.inspect();
    }
  }
}
