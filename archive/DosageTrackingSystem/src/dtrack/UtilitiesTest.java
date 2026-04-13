package dtrack;

import dtrack.dto.Suit;
import dtrack.gateways.*;
import junit.framework.TestCase;

import java.util.*;

public class UtilitiesTest extends TestCase {
  protected void setUp() throws Exception {
    SuitGateway.instance = new InMemorySuitGateway();
  }

  public void testDateGetsTodayWhenNoTestDateIsSpecified() throws Exception {
    Utilities.testDate = null;
    assertTrue(DatesAreVeryClose(new Date(), Utilities.getDate()));
  }

  private boolean DatesAreVeryClose(Date date1, Date date2) {
    GregorianCalendar c1 = new GregorianCalendar();
    GregorianCalendar c2 = new GregorianCalendar();
    c1.setTime(date1);
    c2.setTime(date2);
    long differenceInMS = c1.getTimeInMillis() - c2.getTimeInMillis();
    return Math.abs(differenceInMS) <= 1;
  }

  public void testThatTestDateOverridesNormalDate() throws Exception {
    Date t0 = new GregorianCalendar(1959, 11, 5).getTime();
    Utilities.testDate = t0;
    assertEquals(t0, Utilities.getDate());
  }

  public void testNoSuitsInInventory() throws Exception {
    assertEquals(0, SuitGateway.getNumberOfSuits());
  }

  public void testOneSuitInInventory() throws Exception {
    SuitGateway.add(new Suit(1, new Date()));
    assertEquals(1, SuitGateway.getNumberOfSuits());
  }
}
