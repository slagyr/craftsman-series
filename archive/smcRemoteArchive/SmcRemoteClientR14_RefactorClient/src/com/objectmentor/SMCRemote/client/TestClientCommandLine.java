package com.objectmentor.SMCRemote.client;

import junit.framework.*;
import junit.swingui.TestRunner;

public class TestClientCommandLine extends TestCase {
  public static void main(String[] args) {
    TestRunner.main(new String[]{"TestClientCommandLine"});
  }

  public TestClientCommandLine(String name) {
    super(name);
  }

  public void setUp() throws Exception {
  }

  public void tearDown() throws Exception {
  }

  public void testParseSimpleCompileCommandLine() throws Exception {
    ClientCommandLine c = new ClientCommandLine(new String[]{"filename"});
    assertEquals("filename", c.getFilename());
    assertEquals(ClientCommandLine.DEFAULT_HOST, c.getHost());
    assertEquals(Integer.parseInt(ClientCommandLine.DEFAULT_PORT), c.getPort());
    assertEquals(ClientCommandLine.DEFAULT_GENERATOR, c.getGenerator());
    assert(!c.isVerbose());
  }

  public void testParseComplexCompileCommandLine() throws Exception {
    ClientCommandLine c = new ClientCommandLine(new String[]{"-p", "999", "-h", "objectmentor.com", "-v", "-g", "C++", "f.sm"});
    assert(c.isValid());
    assertEquals("f.sm", c.getFilename());
    assertEquals("bad host", "objectmentor.com", c.getHost());
    assertEquals("bad port", 999, c.getPort());
    assertEquals("bad generator", "C++", c.getGenerator());
    assert("verbose", c.isVerbose());
  }

  public void testRegistrationCommandLine() throws Exception {
    ClientCommandLine c = new ClientCommandLine(new String[]{"-r","user"});
    assert("registration commandline", c.isValid());
  }

  public void testParseInvalidCommandLine() {
    assert("no arguments", !checkCommandLine(new String[0]));
    assert("no filename", !checkCommandLine(new String[]{"-h", "dodah.com"}));
    assert("too many files", !checkCommandLine(new String[]{"file1", "file2"}));
    assert("Bad Argument", !checkCommandLine(new String[]{"-x", "file1"}));
    assert("Bad Port", !checkCommandLine(new String[]{"-p", "bad port"}));
    assert("generator but no file name", !checkCommandLine(new String[]{"-g","C++"}));
  }

  private boolean checkCommandLine(String[] args) {
    ClientCommandLine c = new ClientCommandLine(args);
    return c.isValid();
  }

}