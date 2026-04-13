
import junit.framework.TestCase;

import java.io.*;
import java.util.HashSet;
import java.net.Socket;

public class SMCRemoteServerTest extends TestCase {
  public void testBuildCommandLine() throws Exception {
    assertEquals("java -cp C:/SMC/smc.jar smc.Smc -f myFile.sm",
                 SMCRemoteServer.buildCommandLine("myFile.sm"));
  }

  public void testExecuteCommand() throws Exception {
    String sourceFileName = "simpleSourceFile.sm";
    File sourceFile = writeSourceFile(sourceFileName);

    String command = SMCRemoteServer.buildCommandLine(sourceFileName);
    assertEquals(true, SMCRemoteServer.executeCommand(command));

    File outputFile = new File("F.java");
    assertTrue(outputFile.exists());
    assertTrue(outputFile.delete());
    assertTrue(sourceFile.delete());
  }

  private File writeSourceFile(String theSourceFileName) throws IOException {
    File sourceFile = new File(theSourceFileName);
    PrintWriter pw = new PrintWriter(new FileWriter(sourceFile));
    pw.println("Context C");
    pw.println("FSMName F");
    pw.println("Initial I");
    pw.println("{I{E I A}}");
    pw.close();
    return sourceFile;
  }

  public void testMakeWorkingDirectory() throws Exception {
    File workingDirectory = SMCRemoteServer.makeWorkingDirectory();
    assertTrue(workingDirectory.exists());
    assertTrue(workingDirectory.isDirectory());
    SMCRemoteServer.deleteWorkingDirectory(workingDirectory);
  }

  public void testDeleteWorkingDirectory() throws Exception {
    File workingDirectory = SMCRemoteServer.makeWorkingDirectory();
    File someFile = new File(workingDirectory, "someFile");
    someFile.createNewFile();
    assertTrue(someFile.exists());

    File subdirectory = new File(workingDirectory, "subdirectory");
    subdirectory.mkdir();
    File subFile = new File(subdirectory, "subfile");
    subFile.createNewFile();
    assertTrue(subFile.exists());

    SMCRemoteServer.deleteWorkingDirectory(workingDirectory);
    assertFalse(someFile.exists());
    assertFalse(subFile.exists());
    assertFalse(subdirectory.exists());
    assertFalse(workingDirectory.exists());
  }

  public void testWorkingDirectoriesAreUnique() throws Exception {
    File wd1 = SMCRemoteServer.makeWorkingDirectory();
    File wd2 = SMCRemoteServer.makeWorkingDirectory();
    assertFalse(wd1.getName().equals(wd2.getName()));
    SMCRemoteServer.deleteWorkingDirectory(wd1);
    SMCRemoteServer.deleteWorkingDirectory(wd2);
  }

  public void testCompileIsRunInEmptyDirectory() throws Exception {
    File dummy = new File("dummyFile");
    dummy.createNewFile();

    CompileFileTransaction cft = new CompileFileTransaction("dummyFile");
    dummy.delete();

    CompilerResultsTransaction crt = SMCRemoteServer.compile(cft, "ls >files");

    File files = new File("files");
    assertFalse(files.exists());

    crt.write();
    assertTrue(files.exists());

    BufferedReader reader = new BufferedReader(new FileReader(files));
    HashSet lines = new HashSet();
    String line;
    while ((line = reader.readLine()) != null) {
      lines.add(line);
    }
    reader.close();
    files.delete();

    assertTrue(lines.contains("files"));
    assertTrue(lines.contains("dummyFile"));
    assertEquals(2, lines.size());
  }

  public void testServerEndToEnd() throws Exception {
    SMCRemoteServer server = new SMCRemoteServer(999);
    Socket client = new Socket("localhost", 999);
    ObjectInputStream is = new ObjectInputStream(client.getInputStream());
    ObjectOutputStream os = new ObjectOutputStream(client.getOutputStream());
    String header = (String) is.readObject();
    assertTrue(header.startsWith("SMCR"));
    File sourceFile = writeSourceFile("mySourceFile.sm");
    CompileFileTransaction cft = new CompileFileTransaction("mySourceFile.sm");
    os.writeObject(cft);
    os.flush();
    Thread.sleep(500);
    CompilerResultsTransaction crt = (CompilerResultsTransaction) is.readObject();
    assertNotNull(crt);
    File resultFile = new File("F.java");
    assertFalse(resultFile.exists());
    crt.write();
    assertTrue(resultFile.exists());
    resultFile.delete();
    sourceFile.delete();
  }
}
