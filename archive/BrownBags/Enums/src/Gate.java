public class Gate {
  private enum State {
    LOCKED {
      void badge(Gate g) {
        g.unlock();
        g.itsState = UNLOCKED;
      }

      void pass(Gate g) {
        g.alarm();
      }
    },
    UNLOCKED {
      void badge(Gate g) {
        g.errorTone();
      }

      void pass(Gate g) {
        g.lock();
        g.itsState = LOCKED;
      }
    };

    abstract void badge(Gate g);
    abstract void pass(Gate g);
  }

  private State itsState = State.LOCKED;

  private void lock() {}
  private void unlock() {}
  private void errorTone() {}
  private void alarm() {}

  public void badge() {
    itsState.badge(this);
  }

  public void pass() {
    itsState.pass(this);
  }
}
