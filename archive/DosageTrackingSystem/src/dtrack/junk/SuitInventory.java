package dtrack.junk;

import java.util.*;

public class SuitInventory {
  private List<MensSuit> mensSuits = new ArrayList<MensSuit>();
  private List<WomensSuit> womensSuits = new ArrayList<WomensSuit>();

  void inspect(List<? extends Suit> suits) {
      for (Suit s : suits) {
        // inspect s.
      }
  }

  void inspectWomensSuits() {
    inspect(womensSuits);
  }

  public void getSuitsOverdueForInspection(List<? super Suit> overdueSuits) {
    getSuitsOverdueForInspectionFromList(mensSuits, overdueSuits);
    getSuitsOverdueForInspectionFromList(womensSuits, overdueSuits);
  }

  private void getSuitsOverdueForInspectionFromList(List<? extends Suit> suitList, List<? super Suit> overdueSuits) {
    for (Suit s : suitList)
      if (s.overdueForInspection())
        overdueSuits.add(s);
  }

  public List<Suit> getSuits() {
    List<Suit> suits = new ArrayList<Suit>();
    suits.addAll(mensSuits);
    suits.addAll(womensSuits);
    return suits;
  }

  public void addSuit(Suit suit) {
    if (suit instanceof MensSuit)
      mensSuits.add((MensSuit)suit);
    else
      womensSuits.add((WomensSuit)suit);
  }
}
