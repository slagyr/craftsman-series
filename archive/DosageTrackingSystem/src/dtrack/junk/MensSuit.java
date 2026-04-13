package dtrack.junk;

public class MensSuit extends Suit {
  int ipa;
  int ipb;
  int ipc;

  public MensSuit(int barCode) {
    super(barCode);
  }

  public void accept(SuitVisitor v) {
    if (v instanceof MensSuitVisitor)
      ((MensSuitVisitor)v).visit(this);
  }
}
