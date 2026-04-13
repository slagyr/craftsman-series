package junk;

import java.util.List;

public class ValveInventory {
  public void getValvesOverdueForInspection(List<? super Valve> overdueItems) {
    overdueItems.add(new Valve());
  }
}
