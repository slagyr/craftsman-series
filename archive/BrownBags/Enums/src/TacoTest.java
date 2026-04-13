import junit.framework.TestCase;

public class TacoTest extends TestCase {
  public void testIsTooHot() throws Exception {
    assertTrue(Taco.Salsa.hot.isTooHotForMe());
    assertFalse(Taco.Salsa.medium.isTooHotForMe());
    assertFalse(Taco.Salsa.mild.isTooHotForMe());
  }

  public void testSips() throws Exception {
    assertEquals(0,Taco.Salsa.mild.sips());
    assertEquals(1, Taco.Salsa.medium.sips());
    assertEquals(5, Taco.Salsa.hot.sips());
  }
}
