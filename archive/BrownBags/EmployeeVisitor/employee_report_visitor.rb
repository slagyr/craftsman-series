class EmployeeReportVisitor
  def visitHourly(hourlyEmployee)
    name = hourlyEmployee.getName
    hours = hourlyEmployee.getHours
    @reportLine = "Hourly #{name} worked #{hours} hours."   
  end
  
  def visitSalaried(salariedEmployee)
    name = salariedEmployee.getName
    salary = salariedEmployee.getSalary
    @reportLine = "Salaried #{name} earns $#{salary}."
  end
  
  def getReportLine 
    @reportLine
  end
end