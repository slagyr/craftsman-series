package dtrack.fixtures;

import dtrack.Utilities;
import fit.ColumnFixture;

public class SuitRegistrationRequest extends ColumnFixture {
  public int barCode;
  public void execute() {
    Utilities.manufacturing.requestApprovalForRegistration(barCode);
  }
}
