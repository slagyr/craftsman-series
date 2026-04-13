public class EmployeeReportVisitor implements EmployeeVisitor {
  private String reportLine;

  public String getReportLine() {
    return reportLine;
  }

  public void visit(HourlyEmployee hourlyEmployee) {
    int hours = hourlyEmployee.getHours();
    String name = hourlyEmployee.getName();
    reportLine = String.format("Hourly %s worked %d hours.", name, hours);
  }

  public void visit(SalariedEmployee salariedEmployee) {
    String name = salariedEmployee.getName();
    int salary = salariedEmployee.getSalary();
    reportLine = String.format("Salaried %s earns $%d.", name, salary);
  }
}
