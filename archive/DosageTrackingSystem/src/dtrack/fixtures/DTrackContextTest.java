package dtrack.fixtures;

import dtrack.Utilities;
import junit.framework.TestCase;

import java.util.*;

public class DTrackContextTest extends TestCase { 
  public void testExecuteSetsTestDate() throws Exception {
    Date t0 = new GregorianCalendar(1959, 11, 5).getTime();
    DTrackContext c = new DTrackContext();
    c.todaysDate = t0;
    Utilities.testDate = null;
    c.execute();
    assertEquals(t0, Utilities.testDate);
  }
}
