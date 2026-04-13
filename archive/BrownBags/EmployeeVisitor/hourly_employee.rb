require 'employee'

class HourlyEmployee < Employee
  def initialize(name) 
    super(name)
    @timeCards = []
  end
  
  def addTimeCard(timeCard)
    @timeCards << timeCard;
  end
  
  def accept(visitor)
    visitor.visitHourly(self)
  end
  
  def getHours
    hours = 0;
    @timeCards.each {|tc| hours += tc.getHours}
    hours
  end
end