package com.objectmentor.SMCRemote.server;

import com.objectmentor.SMCRemote.transactions.*;

import java.io.*;
import java.net.*;

class ServerSession extends ServerController {
  private ObjectOutputStream serverOutput;
  private ObjectInputStream serverInput;
  private String itsSessionID;
  private Socket itsSocket;
  private Exception itsException;
  private SMCRemoteServer itsParent;

  private String registrationFailureReason = null;

  private CompileFileTransaction cft;
  private File tempDirectory;
  private CompilerResultsTransaction crt;
  private LoginTransaction lt;
  private RegistrationTransaction rt;

  public ServerSession(SMCRemoteServer parent, Socket socket) throws IOException {
    itsParent = parent;
    itsSocket = socket;
    buildSessionID();
    serverOutput = new ObjectOutputStream(itsSocket.getOutputStream());
    serverInput = new ObjectInputStream(itsSocket.getInputStream());
  }

  public ObjectInputStream initializeSession(Socket socket) throws IOException {
    serverOutput.writeObject("SMCR Server. " + SMCRemoteService.VERSION);
    writeMessageFile();
    serverOutput.flush();
    return serverInput;
  }

  private void writeMessageFile() throws IOException {
    if (SMCRemoteService.messageFile == null) {
      serverOutput.writeObject(null);
    } else {
      StringBuffer b = new StringBuffer();
      BufferedReader br = new BufferedReader(new FileReader(SMCRemoteService.messageFile));
      String line;
      while ((line = br.readLine()) != null)
        b.append(line + "\n");
      serverOutput.writeObject(b.toString());
    }
  }

  public void compileEvent(CompileFileTransaction cft) {
    this.cft = cft;
    compileEvent();
  }

  public void loginEvent(LoginTransaction lt) {
    this.lt = lt;
    loginEvent();
  }

  public void registerEvent(RegistrationTransaction rt) {
    this.rt = rt;
    verboseMessage(rt.getUsername() + " requests registration.");
    registerEvent();
  }

  public String generatePassword() {
    verboseMessage("Generating password.");
    return PasswordGenerator.generatePassword();
  }

  public void storeUserAndSendPassword() {
    String password = generatePassword();
    String username = rt.getUsername();
    try {
      boolean stored = SMCRemoteService.addUser(username, password);
      if (stored) {
        verboseMessage("User stored.  Sending password email.");
        boolean emailSent = sendPasswordEmail(username, password);
        if (emailSent)
          userStoredEvent();
        else {
          verboseMessage("could not send email.");
          registrationFailureReason = "could not send email.";
          sendFailedEvent();
        }
      } else {
        verboseMessage("Duplicate Registration");
        password = SMCRemoteService.getPassword(username);
        resendPasswordEmail(username, password);
        registrationFailureReason = "already a member. Email resent.";
        userNotStoredEvent();
      }
    } catch (Exception e) {
      abort(e);
    }
  }

  private boolean resendPasswordEmail(String username, String password) {
    return SMCRemoteService.sendEmail(username,
                                      "SMCRemote Resending password",
                                      "Your SMCRemote password is: " + password);
  }

  private boolean sendPasswordEmail(String username, String password) {
    return SMCRemoteService.sendEmail(username,
                                      "SMCRemote Registration Confirmation",
                                      "Your password is: " + password);
  }

  public void confirmRegistration() {
    verboseMessage("Confirming registration.");
    try {
      RegistrationResponseTransaction rrt = new RegistrationResponseTransaction(true);
      sendToClient(rrt);
    } catch (Exception e) {
      abort(e);
    }
  }

  public void denyRegistration() {
    verboseMessage("Registration Denied: " + registrationFailureReason);
    try {
      RegistrationResponseTransaction rrt = new RegistrationResponseTransaction(false);
      rrt.setFailureReason(registrationFailureReason);
      sendToClient(rrt);
    } catch (IOException e) {
      abort(e);
    }
  }

  public void checkValidUser() {
    String userName = lt.getUserName();
    String password = lt.getPassword();
    if (SMCRemoteService.validate(userName, password))
      validUserEvent();
    else
      invalidUserEvent();
  }

  public void close() {
    itsParent.close();
    closeEvent();
  }

  public void acknowledgeLogin() {
    try {
      int logins = SMCRemoteService.incrementLoginCount(lt.getUserName());
      verboseMessage("Login(" + logins + "): " + lt.getUserName() + " accepted.");
      LoginResponseTransaction lrt = new LoginResponseTransaction(true, logins);
      sendToClient(lrt);
    } catch (Exception e) {
      abort(e);
    }
  }

  public void rejectLogin() {
    verboseMessage("Login: " + lt.getUserName() + " rejected.");
    try {
      LoginResponseTransaction lrt = new LoginResponseTransaction(false, 0);
      sendToClient(lrt);
    } catch (IOException e) {
      abort(e);
    }
  }

  public void doCompile() {
    verboseMessage("Compiling: " + cft.getFilename() + " using " + cft.getGenerator() + " generator.");

    try {
      tempDirectory = SMCRemoteService.makeTempDirectory();
      cft.write(tempDirectory);

      compile();
      goodCompileEvent();
    } catch (Exception e) {
      abort(e);
    }
  }

  public void sendCompileResults() {
    try {
      sendCompilerResultsToClient();
      tempDirectory.delete();
    } catch (IOException e) {
      abort(e);
    }
  }

  public void sendCompileRejection() {
    verboseMessage("Not logged in, can't compile.");
    CompilerResultsTransaction crt = new CompilerResultsTransaction();
    crt.setStatus(CompilerResultsTransaction.NOT_LOGGED_IN);
    try {
      sendToClient(crt);
    } catch (IOException e) {
      abort(e);
    }
  }

  public void sendCompileError() {

  }

  public void reportError() {
    System.out.println("Aborting: " + itsException);
  }

  private void abort(Exception e) {
    itsException = e;
    abortEvent();
  }

  private void buildSessionID() {
    InetAddress addr = itsSocket.getInetAddress();
    String connectedHostName = addr.getHostName();
    String connectedIP = addr.getHostAddress();
    if (connectedHostName.equals(connectedIP)) {
      itsSessionID = connectedHostName + ":" + itsSocket.getPort();
    } else {
      itsSessionID = connectedHostName + ":" + itsSocket.getPort() + "(" + connectedIP + ")";
    }
  }

  private void compile() throws Exception {
    String filename = cft.getFilename();
    crt = new CompilerResultsTransaction();
    File batFile = writeCompileScript();

    SMCRemoteService.executeCommand(tempDirectory + "\\smc.bat", crt.getStdoutLines(), crt.getStderrLines());

    batFile.delete();
    File sourceFile = new File(tempDirectory, filename);
    sourceFile.delete();
  }

  private File writeCompileScript() throws IOException {
    File batFile = new File(tempDirectory, "smc.bat");
    PrintWriter bat = new PrintWriter(new FileWriter(batFile));
    bat.println("cd " + tempDirectory);
    bat.println(SMCRemoteService.buildCommand(cft.getFilename(), cft.getGenerator()));
    bat.close();
    return batFile;
  }

  private void sendCompilerResultsToClient() throws IOException {
    String[] filenames = tempDirectory.list();
    crt.loadFiles(tempDirectory, filenames);
    crt.setStatus(CompilerResultsTransaction.OK);
    verboseSendFileReport(filenames);
    sendToClient(crt);
    deleteCompiledFiles(filenames);
  }

  private void sendToClient(SocketTransaction t) throws IOException {
    serverOutput.writeObject(t);
    serverOutput.flush();
  }

  private void deleteCompiledFiles(String[] filenames) {
    for (int i = 0; i < filenames.length; i++) {
      File f = new File(tempDirectory, filenames[i]);
      f.delete();
    }
  }

  private void verboseSendFileReport(String[] filenames) {
    for (int i = 0; i < filenames.length; i++) {
      String filename = filenames[i];
      verboseMessage("Sending: " + filename);
    }
  }

  public void verboseMessage(String msg) {
    SMCRemoteService.verboseMessage("<" + itsSessionID + "> " + msg);
  }

}

