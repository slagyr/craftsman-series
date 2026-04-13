public interface EmployeeVisitor {
  void visit(HourlyEmployee hourlyEmployee);
  void visit(SalariedEmployee salariedEmployee);
}
