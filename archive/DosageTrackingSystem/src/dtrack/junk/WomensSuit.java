package dtrack.junk;

public class WomensSuit extends Suit {
  int ipa;
  int ipb;

  public WomensSuit(int barCode) {
    super(barCode);
  }

  public void accept(SuitVisitor v) {
    if (v instanceof WomensSuitVisitor)
    ((WomensSuitVisitor)v).visit(this);
  }
}
