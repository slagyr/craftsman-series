package com.objectmentor.SMCRemote.server;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.*;

public class UserRepository implements UserDirectory {

  class User {
    public User() {
    }

    public User(String username, String password, int loginCount) {
      this.username = username;
      this.password = password;
      this.loginCount = loginCount;
    }

    String username;
    String password;
    int loginCount;
  }

  private File userDirectory;

  public UserRepository(String userDirectoryName) {
    userDirectory = makeUserDirectory(userDirectoryName);
  }

  private File makeUserDirectory(String userDirectoryName) {
    File directory = new File(userDirectoryName);
    if (!directory.exists())
      directory.mkdir();
    return directory;
  }

  public boolean isValid(String username, String password) {
    return password.equals(getPassword(username));
  }

  public String getPassword(String username) {
    User user = readUser(username);
    return user.password;
  }

  private User readUser(String username) {
    User user = new User();
    try {
      File userFile = new File(userDirectory, username);
      if (userFile.canRead()) {
        SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        Document userDoc = builder.build(userFile);
        Element userElement = userDoc.getRootElement();
        user.username = userElement.getChild("name").getTextTrim();
        user.password = userElement.getChild("password").getTextTrim();
        user.loginCount = Integer.parseInt(userElement.getChild("loginCount").getTextTrim());
      }
    } catch (JDOMException e) {
      System.out.println("e = " + e);
      return null;
    }
    return user;
  }

  public boolean add(String username, String password) throws Exception {
    File userFile = new File(userDirectory, username);
    if (userFile.exists() == false) {
      User user = new User(username, password, 0);
      writeUser(user);
      return true;
    } else {
      return false;
    }
  }

  private void writeUser(User user) throws IOException {
    File userFile = new File(userDirectory, user.username);
    FileOutputStream os = new FileOutputStream(userFile);
    Document userDoc = createUserDocument(user);
    XMLOutputter xmlOut = new XMLOutputter();
    xmlOut.output(userDoc, os);
    os.close();
  }

  private Document createUserDocument(User user) {
    Element userElement = new Element("user");
    userElement.addContent(new Element("name").setText(user.username));
    userElement.addContent(new Element("password").setText(user.password));
    userElement.addContent(new Element("loginCount").setText(Integer.toString(user.loginCount)));
    Document userDoc = new Document(userElement);
    return userDoc;
  }

  public boolean clearUserRepository() {
    boolean cleared = true;
    File files[] = userDirectory.listFiles();
    for (int i = 0; i < files.length; i++) {
      File file = files[i];
      if (file.delete() == false)
        cleared = false;
    }
    if (userDirectory.delete() == false)
      cleared = false;
    return cleared;
  }

  public int incrementLoginCount(String username) throws Exception {
    User user = readUser(username);
    user.loginCount++;
    writeUser(user);
    return user.loginCount;
  }
}
