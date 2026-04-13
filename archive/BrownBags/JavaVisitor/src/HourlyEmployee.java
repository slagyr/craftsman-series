import java.util.ArrayList;
import java.util.List;

public class HourlyEmployee extends Employee {
  private List<TimeCard> timeCards;
  public HourlyEmployee(String name) {
    super(name);
    timeCards = new ArrayList<TimeCard>();
  }

  public void addTimeCard(TimeCard timeCard) {
    timeCards.add(timeCard);
  }

  public void accept(EmployeeVisitor v) {
    v.visit(this);
  }

  public int getHours() {
    int hours = 0;
    for (TimeCard tc : timeCards)
      hours += tc.getHours();
    return hours;
  }
}
