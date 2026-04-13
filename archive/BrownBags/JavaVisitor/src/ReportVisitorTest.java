import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

public class ReportVisitorTest {
  private HourlyEmployee hal;
  private SalariedEmployee sam;
  private EmployeeReportVisitor v;

  @Before
  public void createEmployees() {
    hal = new HourlyEmployee("Hal");
    hal.addTimeCard(new TimeCard(8));

    sam = new SalariedEmployee("Sam", 500);
    v = new EmployeeReportVisitor();
  }

  @Test
  public void hourlyEmployeeReportsHours() {
    Employee e = hal;
    e.accept(v);
    assertEquals("Hourly Hal worked 8 hours.", v.getReportLine());
  }

  @Test
  public void salariedEmployeeReportsSalary() {
    Employee e = sam;
    e.accept(v);
    assertEquals("Salaried Sam earns $500.", v.getReportLine());
  }
}
