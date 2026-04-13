package dtrack.fixtures;

import dtrack.gateways.*;
import fit.ColumnFixture;

public class SuitInventoryParameters extends ColumnFixture {
  public int numberOfSuits() {
    return SuitGateway.getNumberOfSuits();
  }
}
