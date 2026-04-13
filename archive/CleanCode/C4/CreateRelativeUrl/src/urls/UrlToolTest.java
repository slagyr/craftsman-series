package urls;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.net.URL;

public class UrlToolTest {
  @Test
  public void canCreateSimpleRelativeUrl() throws Exception {
    URL base = new URL("http://www.alphonse.net/alpha/");
    URL full =  new URL("http://www.alphonse.net/alpha/beta");
    String relativeUrl = UrlTool.createRelativeURL(full, base);
    assertEquals("beta", relativeUrl);
  }

  @Test
  public void canHandleBackwardsReference() throws Exception {
    URL base = new URL("http://www.alphonse.net/alpha/beta/");
    URL full =  new URL("http://www.alphonse.net/alpha");
    String relativeUrl = UrlTool.createRelativeURL(full, base);
    assertEquals("../../alpha", relativeUrl);
  }

}
