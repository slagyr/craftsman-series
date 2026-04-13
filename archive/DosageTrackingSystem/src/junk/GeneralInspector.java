package junk;

import dtrack.junk.SuitInventory;

import java.util.*;

public class GeneralInspector {
  private SuitInventory suits = new SuitInventory();
  private ValveInventory valves = new ValveInventory();

  public List<Object> getOverdueItems() {
    List<Object> overdueItems = new ArrayList<Object>();
    suits.getSuitsOverdueForInspection(overdueItems);
    valves.getValvesOverdueForInspection(overdueItems);
    return overdueItems;
  }

  public void PrintOverdueInspectionReport() {
    List<Object> overdueItems = getOverdueItems();
    for (Object overdueItem : overdueItems) {
      System.out.printf("%s is overdue\n", overdueItem);
    }
  }
}
