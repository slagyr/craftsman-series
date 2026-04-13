# The Craftsman: 13 SMCRemote Part III Objects

Robert C. Martin
21 April, 2003

*...Continued from last month. See > for last month's article, and the code we were working on. You can download that code from:*

*[Craftsman_12_SMCRemote_I1_ThreeUglyLines.zip](archive/Craftsman_12_SMC_II_ThreeUglyLines.zip)*

At our current `τ` of .045 *Destination* was still many lifetimes in the future. Each generation since *Departure* felt those lifetimes stretch inexhaustibly before them. Sometimes the feeling was depressing -- but not today. Today I used a bit of that inexhaustible time to taunt Jerry.

I stretched my hands out in front of the keyboard and cracked my knuckles. I rocked my head back and forth, pretending to work out the kinks. I paused, gazing off into the distance, putting on an air of deep contemplation. "Oh Yes!" I said, "I think I know a better way!" Jerry rolled his eyes and sighed, waiting for me to get on with it. I decided I had better not push my luck, so I started to work.

In order to write a file across the socket, Jerry had sent three lines of text first. One had the string "Sending", the next had the name of the file, and the last was length of the file. Then Jerry sent the file itself as an array of chars. The code looked like this:

```java
    private void writeSendFileCommand() throws IOException {
        os.println("Sending");
        os.println(itsFilename);
        os.println(itsFileLength);
        char buffer[] = new char[(int) itsFileLength];
        fileReader.read(buffer);
        os.write(buffer);
        os.flush();
    }
```

When reading the file back in from the socket, he called readLine three times, once for each of the three lines he sent. He used the "Sending" string as a transaction identifier. He saved the second as the file name. The third was the length of the file. He used it to allocate an array of chars to use as a buffer. Then he used the length to read the appropriate number of chars from the socket.

```java
  private void parse(String cmd) throws Exception {
    if (cmd != null) {
      if (cmd.equals("Sending")) {
        filename = is.readLine();
        fileLength = Long.parseLong(is.readLine());
        content = new char[(int)fileLength];
        is.read(content,0,(int)fileLength);
        fileReceived = true;
      }
    }
  }
```

This all worked just fine, but I thought it was ugly. I was convinced there was a better way. So I started to make some simple changes. First I changed the test to read objects instead of lines:

```java
  public void serve(Socket socket) {
    try {
      os = new PrintStream(socket.getOutputStream());
      is = new ObjectInputStream(socket.getInputStream());
      os.println("SMCR Test Server");
      os.flush();
      parse((String)is.readObject());
    } catch (Exception e) {
    }
  }

  private void parse(String cmd) throws Exception {
    if (cmd != null) {
      if (cmd.equals("Sending")) {
        filename = (String)is.readObject();
        fileLength = is.readLong();
        content = (char[]) is.readObject();
        fileReceived = true;
      }
    }
  }
```

Next I changed the SMCRemoteClient to write objects instead of strings.

```java
    public boolean connect() {
      ...
      os = new ObjectOutputStream(smcrSocket.getOutputStream());
      ...
    }

    private void writeSendFileCommand() throws IOException {
        os.writeObject("Sending");
        os.writeObject(itsFilename);
        os.writeLong(itsFileLength);
        char buffer[] = new char[(int) itsFileLength];
        fileReader.read(buffer);
        os.writeObject(buffer);
        os.flush();
    }
```

I ran all the tests, and they worked just fine. "See?" I said. "I figured it would be better to write objects than it would be to write strings."

I looked at Jerry, but something had changed. Jerry's eyes weren't focusing. He got up and started to pace. Occasionally he would stop, look at the screen, look at me, shake his head and start pacing again. He kept mumbling something about years, experience, and stupidity. It was a little scary.

Finally he stopped, looked me square in the eye, and said: "Well, Alphonse, you've gone and done it."

"What did I *do*, Jerry?"

He stared at me for another couple of seconds. Then turned towards the turbo and said: "Follow me."

The ride down the turbo was silent. Jerry's mood was hard to read. He wasn't exactly angry, but he was certainly annoyed, and I was somehow involved with his annoyance. As we rode, silently shifting our weight to adjust for the coriolis deflection, I tried to figure out why my simple code change would have such a profound effect upon him.

I followed Jerry into a lounge on one of the low-g levels. Apprentices weren't normally allowed below .49g. I hadn't been reading the wall markers on the way down, but this one felt to be less than .4g. Inside the lounge were five other journeymen programmers. Jerry introduced me to the group. I made sure to remember everyone's name: Johnson, Jasmine, Jason, Jasper, and Jennifer. Jerry told me to stand in the center of the lounge, while he and everyone else sat on couches around me. Then Jerry turned to the group and with a grimace he said:

"Well, it's happened. I believe Alphonse is the first apprentice this year to *Micah* his Journeyman." I felt my heart skip a beat, and my eyes got wide. It was such a simple thing! I hadn't expected *this*!

"Have any of you been Micahed so far this year?" asked Jerry. There was a murmur around the room, but everyone shook their heads. Apparently nobody had.

Jasmine looked at me long and hard. She locked her eyes on mine and said to Jerry: "OK, Jer (She pronounced it Jair). Tell us your story."

Jerry sighed and shrugged. He made a visible effort to gather himself together, and then began to speak.

"As you know, Mr. C. asked me to get the SMCRemote stuff working." They all grunted and nodded. Apparently they all knew about it. "Alphonse and I had spent a day on the SocketServer exercise; and he had done very well."

Another shock: SocketServer had been an*exercise?!!?*

"Once we got it working we started putting together the client portion of SMCRemote. One of our first test cases was to ship a file from the client to the server over the socket." There were more nods and grunts around the room.

Jerry was getting visibly more nervous. He squirmed in his seat and avoided eye contact. "I set up a file-send transaction by shipping three text lines followed by an array of chars. The first line was the transaction id, the second was the file name, and the third was the file length." More nods. This wasn't surprising to any of them. "Clearly this was just a simple way to get the tests to pass so we could refactor into a better form." More nods, more agreeing mumbles.

"And then..." Jerry paused. "And then, Alphonse said he thought he had a -- er -- a better idea."

There was silence in the room. Jasmine's eyes were still locked on mine, but her look shifted from appraisal to speculation. One by one I could sense the gaze of the other journeymen land on me. *What was the big deal?* Why were they claiming a Micah for me?

It was Johnson who broke the spell.

"You don't me to tell us..."

Out of the corner of my eye I could see that Jerry was nodding. Nodding about *what*?

I couldn't stand it any more. I broke away from Jasmine's gaze, looked each of the other journeymen briefly in the eye, and then said: "All I did was suggest that we ship objects instead of strings! I don' t see that as a Micah!"

Jennifer stepped up to me and said: "Yes, that's *all* you did. And, no, I don't suppose *you* think all that much of it. But to us, it's a big deal."

"But why?"

"Because", said Jeremy, "the most important trait of a good programmer is the ability to think abstractly. Very few programmers can actually do that. You have just proven that you can."

I was incredulous. "It was just an object." I said.

"Exactly." said Jennifer. They all nodded earnestly.

I shook my head. "Well then if this is such a good thing -- a Micah, and all -- why is Jerry so annoyed?"

"Oh that!" laughed Jasmine. "Jerry came down here at his last break and told us all about the file length issue you had with him. He was sure you were going to be impressed with him when you saw how that file length just fell into position in the file-send transaction. He was anticipating how awed you would be."

"Yeah", said Jasper, "and you went and showed him how the length was irrelevant."

"I did?"

Jerry stood up and said: "Think about it Alphonse, if you are sending the character array as an object, why do you need to send the length separately? These guys are going to be ribbing me about this for the next six months."

"We sure are!" said Jennifer enthusiastically. "Every time we review any of his code we'll be asking him where the file length parameter is!" She giggled as Jerry grimaced and hung his head.

"You see, Alphonse", said Jasmine, "not only did you make a worthwhile leap of abstraction, your solution was *simpler* than Jerry's. What's more, it was simple in a way that invalidated Jerry's anticipated need for the file length. You *Micahed* him!"

I was beginning to understand. At least I wasn't in any kind of trouble...

"I think", said Jasmine, "that an event like this calls for a change of partners. Jerry, I'll swap apprentices with you. You take Andy, and I'll work with Alphonse for a few days."

...or was I?

*To be continued.*

*The code that Jerry and Alphonse finished can be retrieved from:*

*[Craftsman_13_SMCRemote_III_Objects.zip](archive/Craftsman_13_SMCRemote_III_Objects.zip)*
