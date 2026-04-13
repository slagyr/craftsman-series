package OldSmc;

public abstract class State {
  public abstract void badge(GateFSM g);
  public abstract void pass(GateFSM g);

  public static State locked = new Locked();
  public static State unlocked = new Unlocked();
}
