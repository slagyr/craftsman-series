package dtrack.fixtures;

import dtrack.dto.Suit;
import dtrack.gateways.*;
import fit.RowFixture;

public class SuitsInInventory extends RowFixture {
  public Object[] query() throws Exception {
    return SuitGateway.getArrayOfSuits();
  }

  public Class getTargetClass() {
    return Suit.class;
  }
}
