# The Craftsman: 37 Dosage Tracking XIV Handling Rejection

Robert C. Martin
27 April, 2005

*...Continued from last month.*

*Through the last quarter of 1942 FDR's advisors were pressing him to declare war upon the Axis. The successful detonation of an Atomic bomb in August, and the increasing supply of those terrible weapons and the rockets to deliver them, was emboldening the hawks. And, horrible though it was, it made sense. It seemed unlikely that the U.S. would be unable to hold the technological advantage for long. Eliminating the virulence of fascism might be possible now, but the chance could quickly disappear leaving the U.S. to face a huge and fearsome opponent.*

*The contingent had waited to tell the President about Turing's results. They had checked, and double checked the results. They had improved the computers, the telescopes, the programs, and the observations. But the results would not be denied. A 22km rock named Clyde was almost certainly going to slam into the Pacific at 53km per second on April 29**th**, at 0943GMT.*

*On November 12**th**, 1942 as FDR read the report from the contingent, the certainty of destruction seemed to leave the pages and clamp down upon his skull like an iron fist. He called out to his secretary: "Grace, I have a terrific headache!" Grace Tully found him collapsed over his desk, never to regain consciousness again.*

*22 Feb 2002, 0800*

I walked into the lab, just as people were gathering for the morning stand-up meeting. Avery, I, Jasper, Jerry, Carole, and Jean stood together in a circle. Each of us answered three questions: What did you do yesterday? What do you plan to do today? What's in your way?

When my turn came I said: "Avery and I got the `RegisterNormalSuit` acceptance test to pass. I don't know what I'm doing today, and nothing but that ignorance is in my way." I saw Carole role her eyes at that remark, and Jerry gave a little smirk.

Then it was Avery's turn, and he simply said: "My report is the same as Alphonse's."

After the standup Jean came over and said: "Boys, I think I can solve your problems. Avery, dear, why don't you work with Jerry? He already has his next acceptance test. Alphonse, I'd like you to work with Jasper today. He'll be putting together the acceptance test for manufacturing rejection. I'm sure you and Jasper will get along wonderfully; you are both such fine young men. " She gave us a warm smile and then said "Off with you! Shoo!"

I caught Avery's eye, and gave him a regretful wave, and then headed over to where Jasper was working. He was staring intently at his screen and didn't seem to know I was there. So I said: "Hello Jasper, I guess we're working together today."

Jasper jerked around with a big grin on his face and said "Alphonse! Yeah, great. Hay, should I call you Al, or Fonse? I kind of like Fonse. Whaddya say?"

His toothy grin, and the sparkle in his eyes, put me off guard. I was about to say that "Fonse" is the kind of nickname that sticks, and that I wasn't sure if I really wanted it to stick; but he cut me off and said: "Great! Now let's get busy on this new test."

I sighed and sat down. "Jean said something about a rejection from Manufacturing?"

"Yeah, that's right. Here look at the story card." He handed me the index card from Carole's planning meeting yesterday morning.

"Oh, yeah." I said. "I remember now. Carole told us that if someone tries to register a suit that didn't come from manufacturing, we should reject the registration."

"Right." Said Jasper. "We don't want someone trying to register some old suit they found in a locker somewhere. Eh?"

"Yeah. OK, so we need a new acceptance test for this case, don't we?"

"Right you are, Fonse, I was just working on that when you came up here." He pointed to his screen. "I just created a new page named `SuitRegistrationRejectedByManufacturing`. I took the `RegisterNormalSuit` page that you guys got working yesterday, and pasted it into this new page."

I examined the page, and sure enough it was a perfect copy of the `RegisterNormalSuit` page. "So I guess you want to alter it to reflect that manufacturing rejects the confirmation request?"

"Right again, Fonse. Want to take a crack at it?"

"Uh, sure." I wasn't sure if I liked his over-friendly demeanor. He didn't seem to mean anything by it, but I thought it could get annoying after awhile. I edited the page, changing a few comments and titles. The meat of the difference was to change the message sent my manufacturing to a rejection, and to then assert that the suit did not get placed into inventory. The result, complete with test results, looked like this:

"Nice work!" said Jasper. "That's exactly right. Now, can you make this pass?"

"I think I can." I said. I opened up the fixture that handled the "Message received from manufacturing" table. It looked like this:

```java
public class MessageReceivedFromManufacturing extends ColumnFixture {
  public String messageId;
  public int messageArgument;
  public String messageSender;
  public String messageRecipient;
  public void execute() {
    SuitRegistrationAccepted message =
      new SuitRegistrationAccepted(messageId,
                                   messageArgument,
                                   messageSender,
                                   messageRecipient);
    Registrar.acceptMessageFromManufacturing(message);
  }
}
```

It just passed the table data along to the `acceptMessageFromManufacturing` method of the `Registrar` class. That method looked like this:

```java
public class Registrar {
  public static void acceptMessageFromManufacturing(Object message) {
    SuitRegistrationAccepted suitAck = (SuitRegistrationAccepted) message;
    Suit acceptedSuit = new Suit(suitAck.argument, Utilities.getDate());
    SuitGateway.add(acceptedSuit);
  }
}
```

I made the following simple change:

```java
public class Registrar {
  public static void acceptMessageFromManufacturing(Object message) {
    SuitRegistrationAccepted suitAck = (SuitRegistrationAccepted) message;
    if (suitAck.id.equals("Suit Registration Accepted")) {
      Suit acceptedSuit = new Suit(suitAck.argument, Utilities.getDate());
      SuitGateway.add(acceptedSuit);
    }
  }
}
```

When I ran the test, it passed.

"Nicely done, Fonce; but you didn't write a unit test!"

"Do you really think one is necessary for just this `if` statement?

"How hard would it be to write?"

I shook my head and just typed. It was a simple test to write.

```java
public class RegistrarTest extends TestCase {
  public void testAcceptRegistration() throws Exception {
    final String acceptId = "Suit Registration Accepted";
    SuitRegistrationAccepted suitAck =
      new SuitRegistrationAccepted(acceptId, 9999, "me", "you");
    Registrar.acceptMessageFromManufacturing(suitAck);
    Suit[] suits = (Suit[])SuitGateway.getArrayOfSuits();
    assertEquals(1, suits.length);
    assertEquals(9999, suits[0].barCode());
  }
}
```

The test passed on its first try. I felt a little smug. So as I looked over to Jasper and raised an eyebrow. His supercilious grin framed by his sugarbowl haircut made him look clueless.

"You didn't write the negative case." He said, still grinning widely.

I thought to myself: "This must be how Avery feels." I suppressed the desire to roll my eyes, and simply kept typing.

```java
  public void testRejectRegistration() throws Exception {
    final String rejectId = "Suit Registration Rejected";
    SuitRegistrationAccepted suitNak =
      new SuitRegistrationAccepted(rejectId, 9999, "me", "you");
    Registrar.acceptMessageFromManufacturing(suitNak);
    Suit[] suits = (Suit[])SuitGateway.getArrayOfSuits();
    assertEquals(0, suits.length);
  }
```

This test also worked the first time. So I raised my eyebrow again.

"Hey Fonse, the name of that class isn't quite right, is it?"

I did a silent little curse. I had been hoping he wouldn't notice what I had notice while typing this test. The name `SuitRegistrationAccepted` really wasn't the right name for this class anymore. I hated to admit it, but this is something that the unit test forced me to see, that the acceptance test completely hid from me. So I changed the name to `SuitRegistrationAcceptanceMessage`.

Jasper tilted his head as though thinking about something, and then cheerily said: "Fonse, do you think those message id's should really be long strings? Do you think those strings ought to be scattered around the code? I think that's kind of ugly don't you?"

Drat, he did it again. That was just what I was thinking. Strings like "Suit Registration Accepted" and "Suit Registration Rejected" probably weren't good values to use as message ids. Moreover, they probably shouldn't be scattered around the code. "Yeah, I agree. What do you think we should do about it?"

"I think we should get all the tests to pass, and then refactor those id strings out of the code altogether. Perhaps we can make them independent classes."

"Uh, wait. The tests *do* all pass."

"Have you run them all since that last change you made Fonse?"

"Yeah, I..." Actually I had only run the `testRejectRegistration` test. I hadn't run any of our other unit tests. So I turned back to the screen and pushed the button the ran *all* the existing unit tests in the DTrack system. To my horror, `testRejectRegistration` failed! "Wait! That test just passed!"

"Yeah, I think we've got a database cleanup problem." Smiled Jasper. "Suits are being placed into inventory, and not removed at the start of each test. Try running all the acceptance tests. I'll bet they fail too."

Sure enough, as I pushed the *suite* button on the top level of our acceptance tests, the `RegisterNormalSuit` page passed, but the `SuitRegistrationRejectedByManufacturing` page - the page I had just gotten working ten minutes ago, failed.

Jasper smacked his hands together and started rubbing them. "OK Fonsie, my young apprentice, let's clean this all up."

*The code for this article can be located at:*

[Craftsman_37_DosageTrackingSystem.zip](archive/Craftsman_37_DosageTrackingSystem.zip)

*To be continued...*

http://www.sdmagazine.com/documents/s=7764/sdm0407h/

Register new suit.

Bar Code Patch X(6)

Register new suit, screen function.

Send confirmation to mfg.

Reject registration on denial.

10s time out & reject

If already reg'd

Reject reg & don't send conf to prod.

Sched for inspection.

**Manufacturing rejects a suit registration.**

```
Import
dtrack.fixtures
```

*We assume that today is 2/21/2002.***

```
DTrack Context
Today's date
2/21/2002
```

*We also assume that there are no suits in inventory.***

```
Suit inventory parameters
Number of suits?
0
```

*We attempt to register suit 314159.***

```
Suit Registration Request
bar code
314159
```

*DTrack sends the registration confirmation to Manufacturing.***

```
Message sent to manufacturing
message id?
message argument?
message sender?
Suit Registration
314159
Outside Maintenance
```

*Manufacturing rejects the confirmation.***

```
Message received from manufacturing
message id
message argument
message sender
message recipient
Suit Registration Rejected
314159
Manufacturing
Outside Maintenance
```

*The rejected suit should not be in the registered inventory.***

```
Suits in inventory
bar code?
next inspection date?
314159 surplus
Thu Feb 21 00:00:00 CST 2002
```
