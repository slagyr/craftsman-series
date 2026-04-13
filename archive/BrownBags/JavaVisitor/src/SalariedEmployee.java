public class SalariedEmployee extends Employee {
  private int salary;

  public SalariedEmployee(String name, int salary) {
    super(name);
    this.salary = salary;
  }

  public void accept(EmployeeVisitor v) {
    v.visit(this);
  }

  public int getSalary() {
    return salary;
  }
}
