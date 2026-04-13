package dtrack.junk;

public class SuitInspectionReportVisitor implements SuitVisitor, 
                                                    MensSuitVisitor,
                                                    WomensSuitVisitor {
  public String line;

  public void visit(MensSuit ms) {
    line = String.format("%d MS A:%d, B:%d, C:%d",
                         ms.barcode, ms.ipa, ms.ipb, ms.ipc);
  }

  public void visit(WomensSuit ws) {
    line = String.format("%d WS A:%d, B:%d",
                         ws.barcode, ws.ipa, ws.ipb);
  }
}
