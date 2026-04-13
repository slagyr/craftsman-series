package dtrack.junk;

public abstract class Suit {
  protected int barcode;

  public Suit(int barcode) {
    this.barcode = barcode;
  }

  public abstract void accept(SuitVisitor v);

  public boolean overdueForInspection() {
    return false;
  }
}
