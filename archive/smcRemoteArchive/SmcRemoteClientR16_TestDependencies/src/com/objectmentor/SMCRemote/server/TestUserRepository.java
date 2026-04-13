package com.objectmentor.SMCRemote.server;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

public class TestUserRepository extends TestCase {
  private UserRepository repository;

  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestUserRepository"});
  }

  public TestUserRepository(String name) {
    super(name);
  }

  public void setUp() throws Exception {
    repository = new UserRepository("testUsers");
  }

  public void tearDown() throws Exception {
    assert("Repository not cleared", repository.clearUserRepository());
  }

  public void testEmptyRepository() throws Exception {
    assertEquals("EmptyRepository", false, repository.isValid("rmartin@oma.com", "password"));
  }

  public void testAdd() throws Exception {
    repository.add("rmartin@oma.com", "password");
    assertEquals("Add", true, repository.isValid("rmartin@oma.com", "password"));
  }

  public void testwrongPassword() throws Exception {
    assertEquals("addFailed", true, repository.add("rmartin@oma.com", "password"));
    assertEquals("wrongPassword", false, repository.isValid("rmartin@oma.com", "xyzzy"));
  }

  public void testDuplicateAdd() throws Exception {
    assertEquals("FirstAdd", true, repository.add("rmartin@oma.com", "password"));
    assertEquals("DuplicateAdd", false, repository.add("rmartin@oma.com", "password"));
  }
}