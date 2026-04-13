package dtrack.junk;

public class SuitInspectionReport {
  public String generate(SuitInventory inventory) {
    StringBuffer report = new StringBuffer();
    SuitInspectionReportVisitor v = new SuitInspectionReportVisitor();
    for (Suit s : inventory.getSuits()) {
      s.accept(v);
      report.append(v.line).append("\n"); 
    }
    return report.toString();
  }
}
