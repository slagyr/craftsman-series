package com.objectmentor.SMCRemote.server;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.*;

public class UserRepository implements UserDirectory {
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
    String password = null;
    try {
      File userFile = new File(userDirectory, username);
      if (userFile.canRead()) {
        SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        Document userDoc = builder.build(userFile);
        Element user = userDoc.getRootElement();
        password = user.getChild("password").getTextTrim();
      }
    } catch (JDOMException e) {
      System.out.println("e = " + e);
    }
    return password;
  }

  public boolean add(String username, String password) throws Exception {
    File userFile = new File(userDirectory, username);
    if (userFile.exists() == false) {
      addNewUser(userFile, username, password);
      return true;
    } else {
      return false;
    }
  }

  private void addNewUser(File userFile, String username, String password) throws Exception {
    userFile.createNewFile();
    FileOutputStream os = new FileOutputStream(userFile);
    Document userDoc = createUserDocument(username, password);
    XMLOutputter xmlOut = new XMLOutputter();
    xmlOut.output(userDoc, os);
    os.close();
  }

  private Document createUserDocument(String username, String password) {
    Element user = new Element("user");
    user.addContent(new Element("name").setText(username));
    user.addContent(new Element("password").setText(password));
    Document userDoc = new Document(user);
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
}
