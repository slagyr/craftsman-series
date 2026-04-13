package OldSmc;

public class Unlocked extends State {
  public void badge(GateFSM g) {
    g.errorTone();
  }

  public void pass(GateFSM g) {
    g.lock();
    g.state = State.locked;
  }
}
