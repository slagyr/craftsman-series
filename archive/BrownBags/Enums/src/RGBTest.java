import junit.framework.TestCase;

public class RGBTest extends TestCase {
  public void testIsBlue() throws Exception {
    assertTrue(RGB.blue.isBlue());
    assertFalse(RGB.red.isBlue());
    assertFalse(RGB.green.isBlue());
  }
}
