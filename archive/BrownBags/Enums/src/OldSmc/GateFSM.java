package OldSmc;

public class GateFSM extends Gate {
  State state = State.locked;

  public void badge() {
    state.badge(this);
  }
  public void pass() {
    state.pass(this);
  }
}
