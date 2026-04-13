package dtrack;

import dtrack.mocks.MockManufacturing;

import java.util.Date;

public class Utilities {
  public static Date testDate = null;
  public static MockManufacturing manufacturing = new MockManufacturing();

  public static Date getDate() {
    return testDate != null ? testDate : new Date();
  }
}
