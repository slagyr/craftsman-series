package NestedSwitch;

public class Gate {
  private enum State {LOCKED, UNLOCKED}
  private enum Event {BADGE, PASS}

  private State state = State.LOCKED;

  private void lock() {}
  private void unlock() {}
  private void errorTone() {}
  private void alarm() {}

  public void badge() {
    processEvent(Event.BADGE);
  }

  public void pass() {
    processEvent(Event.PASS);
  }

  private void processEvent(Event event) {
    switch (event) {
      case BADGE:
        switch (state) {
          case LOCKED:
            unlock();
            state = State.UNLOCKED;
            break;
          case UNLOCKED:
            errorTone();
            break;
        }
        break;
      case PASS:
        switch (state) {
          case LOCKED:
            alarm();
            break;
          case UNLOCKED:
            lock();
            state = State.LOCKED;
            break;
        }
        break;
    }
  }
}
