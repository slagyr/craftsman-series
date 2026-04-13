public abstract class Employee {
  protected String name;

  public Employee(String name) {
    this.name = name;
  }

  public abstract void accept(EmployeeVisitor v);

  public String getName() {
    return name;
  }
}
