package dtrack.mocks;

import junit.framework.TestCase;

public class MockConsoleTest extends TestCase {
  private MockConsole c;

  protected void setUp() throws Exception {
    c = new MockConsole();
  }

  public void testEmptyConsole() throws Exception {
    assertEquals(0, c.numberOfMessages());
  }

  public void testOneMessage() throws Exception {
    c.display("message");
    assertEquals(1, c.numberOfMessages());
    assertTrue(c.hasMessage("message"));
    assertFalse(c.hasMessage("noMessage"));
  }

  public void testTwoMessages() throws Exception {
    c.display("messageOne");
    c.display("messageTwo");
    assertEquals(2, c.numberOfMessages());
    assertTrue(c.hasMessage("messageOne"));
    assertTrue(c.hasMessage("messageTwo"));
    assertFalse(c.hasMessage("no message"));
  }
}
