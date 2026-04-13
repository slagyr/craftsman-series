package dtrack.fixtures;

import dtrack.Utilities;
import dtrack.gateways.*;
import fit.ColumnFixture;

import java.util.Date;

public class DTrackContext extends ColumnFixture {
  public Date todaysDate;

  public void execute() throws Exception {
    Utilities.testDate = todaysDate;
    SuitGateway.instance = new InMemorySuitGateway();
  }
}
