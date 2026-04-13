package dtrack.mocks;

import dtrack.external.IConsole;

import java.util.*;

public class MockConsole implements IConsole  {
  HashSet messages = new HashSet();
  public void display(String message) {
    messages.add(message);
  }

  public int numberOfMessages() {
    return messages.size();
  }

  public boolean hasMessage(String s) {
    return messages.contains(s);
  }
}
