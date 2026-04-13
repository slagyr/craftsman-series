package com.objectmentor.SMCRemote.server;

import com.objectmentor.SMCRemote.transactions.*;

public class TestRegistration extends TestBase {
  private String emailAddress;
  private String mailSubject;
  private String mailText;
  private int emailMessagesSent = 0;

  private EmailSender mockEmailSender = new EmailSender() {
    public boolean send(String emailAddress, String subject, String text) {
      TestRegistration.this.emailAddress = emailAddress;
      TestRegistration.this.mailSubject = subject;
      TestRegistration.this.mailText = text;
      emailMessagesSent++;
      return true;
    }
  };

  private EmailSender mockBadEmailSender = new EmailSender() {
    public boolean send(String emailAddress, String subject, String text) {
      return false;
    }
  };
  private RegistrationResponseTransaction rrt;
  private UserRepository userRepository;
  private String userRepositoryName = "testUsers";

  public TestRegistration(String name) {
    super(name);
  }

  public void tearDown() throws Exception {
    super.tearDown();
    userRepository.clearUserRepository();
  }

  public void setUp() throws Exception {
    super.setUp();
    userRepository = new UserRepository(userRepositoryName);
  }

  public void testRegistration() throws Exception {
    sendRegistration(mockEmailSender);
    assertEquals("emailAddress", "rmartin@oma.com", emailAddress);
    assertEquals("mailSubject", "SMCRemote Registration Confirmation", mailSubject);
    assert("Mail Text", mailText.startsWith("Your password is: "));
    assertEquals("Mail Text Length", 26, mailText.length());
    assertEquals("Registration", true, rrt.isConfirmed());
    String password = mailText.substring(18);
    assertEquals("User Registered", true, SMCRemoteService.validate("rmartin@oma.com", password));
    assertEquals("email count", 1, emailMessagesSent);
  }

  public void testBadEmail() throws Exception {
    sendRegistration(mockBadEmailSender);
    assertEquals("Registration", false, rrt.isConfirmed());
    assertEquals("Reason", "could not send email.", rrt.getFailureReason());
  }

  public void testDoubleRegistration() throws Exception {
    sendRegistration(mockEmailSender);
    sendRegistration(mockEmailSender);
    assertEquals("second registration", false, rrt.isConfirmed());
    assertEquals("second registration reason", "already a member. Email resent.", rrt.getFailureReason());
    assertEquals("emailAddress", "rmartin@oma.com", emailAddress);
    assertEquals("emailSubject", "SMCRemote Resending password", mailSubject);
    assert("emailText", mailText.startsWith("Your SMCRemote password is: "));
    assertEquals("emailTextLength", 36, mailText.length());
    assertEquals("emailCount", 2, emailMessagesSent);
  }

  private void sendRegistration(EmailSender emailSender) throws Exception {
    connectClientToServer();
    service.setEmailSender(emailSender);
    service.setUserDirectory(userRepository);
    try {
      RegistrationTransaction rt = new RegistrationTransaction("rmartin@oma.com");
      sendToServer(rt);
      rrt = (RegistrationResponseTransaction) is.readObject();
    } finally {
      disconnectClientFromServer();
    }
  }

}
