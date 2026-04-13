require 'employee'

class SalariedEmployee < Employee
  def initialize(name, salary)
    super(name)
    @salary = salary
  end
  
  def getSalary
    @salary
  end
  
  def accept(visitor)
    visitor.visitSalaried(self)
  end
end