package dtrack.gateways;

import dtrack.dto.Suit;

import java.util.Date;

import junit.framework.TestCase;

public class InMemorySuiteGatewayTest extends TestCase {
  public void testIsSuitRegistered() throws Exception {
    InMemorySuitGateway g = new InMemorySuitGateway();
    assertFalse(g.isSuitRegistered(314159));
    g.add(new Suit(314159, new Date()));
    assertTrue(g.isSuitRegistered(314159));
  }
}
