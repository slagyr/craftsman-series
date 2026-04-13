package dtrack.junk;

import junit.framework.TestCase;

public class SuitInspectionReportTest extends TestCase {
  public void testReport() throws Exception {
    SuitInventory inv = new SuitInventory();
    inv.addSuit(new MensSuit(314159));
    inv.addSuit(new WomensSuit(399375));
    SuitInspectionReport r = new SuitInspectionReport();
    assertEquals("314159 MS A:0, B:0, C:0\n399375 WS A:0, B:0\n", r.generate(inv));
  }
}
