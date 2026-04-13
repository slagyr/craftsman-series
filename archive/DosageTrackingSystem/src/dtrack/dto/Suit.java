package dtrack.dto;

import java.util.Date;

public class Suit {
  public Suit(int barCode, Date nextInspectionDate) {
    this.barCode = barCode;
    this.nextInspectionDate = nextInspectionDate;
  }

  private int barCode;
  private Date nextInspectionDate;

  public int barCode() {
    return barCode;
  }
  public Date nextInspectionDate() {
    return nextInspectionDate;
  }

  public boolean overdueForInspection() {
    return false;
  }

  public void inspect() {
  }
}
