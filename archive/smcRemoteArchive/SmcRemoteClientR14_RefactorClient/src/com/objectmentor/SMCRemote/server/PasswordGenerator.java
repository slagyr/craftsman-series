package com.objectmentor.SMCRemote.server;

public class PasswordGenerator {
  public static String generatePassword() {
    StringBuffer password = new StringBuffer();
    for (int i=0; i<8; i++) {
      password.append(generateRandomCharacter());
    }
    return password.toString();
  }

  private static char generateRandomCharacter() {
    double x = Math.random();
    x *= 26;
    return (char)('a'+x);
  }
}
