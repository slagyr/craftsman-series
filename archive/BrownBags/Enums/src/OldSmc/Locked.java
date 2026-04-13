package OldSmc;

public class Locked extends State {
  public void badge(GateFSM g) {
    g.unlock();
    g.state = State.unlocked;
  }

  public void pass(GateFSM g) {
    g.alarm();
  }
}
