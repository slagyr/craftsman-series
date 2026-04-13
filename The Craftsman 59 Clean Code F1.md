# The Craftsman: 59 Clean Code: F1 Too Many Arguments

Robert C. Martin
1 July 2009

*August 12, 1945, 10:30 AM Philadelphia*

*General Groves walked up to the secretary at precisely 10:30 AM. This meeting was too critical to be compromised by an error in punctuality. "Hello General." Said Miss Ives. "Mr. Rearden is expecting you. Please go right in."*

*Hank Readen stood up and extended his hand as the General walked into his office. "It is good to see you again, General. How can I help you this time?"*

*Rearden gestured to the chair next to his desk, and Groves took the seat.*

*"Hank, we need steel. Lots of steel."*

*Rearden chuckled. "General, that is a problem I'd be very happy to help you with. How much steel are we talking about, and when do you need it?"*

*The General's eyes fixed upon Rearden's with an intensity that caused the smile to drain from Rearden's face.*

*"Hank, if you tripled your annual production, we'd take it all starting now."*

*Rearden sat back and let out his breath.*

*"Les, that's..."*

*"Look, Hank, we need this. We'll pay you well for it, and loan you the capital to build new physical plant. But this has to be done fast, and it can't go wrong. Frankly, that's why we picked you for this. We knew that if anyone could make the steel we need, it'd be Henry Rearden."*

*The smile slowly returned to Rearden's face. "General, I'll be happy to work with you on this. I have some inkling of what's at stake. But tell me, Les, are you sure it's steel that you need?"*

*Wed, 15 Mar 2002, 11:00*

"No way, Avery, he's just not that good."

"Alphonse, he'll beat the pants off him!"

We were sitting together at my workstation, discussion the latest chess championship.

"Look, Avery, Eddie is going to pound Robie into the back ranks. Did you see how he slaughtered Wheeler?"

"Hell, Alphonse, It doesn't take much to psych Wheeler, he always gives up too many knights."

Jasmine stuck her head up and glared at us. "Will you two hotshots keep a lid on it please, people are trying to work over here."

"Leave the boys alone." Jerry interjected. "I'm interested to hear their view. I think Robie's going to fall like a rotten tree."

Jasmine shifted her glare to Jerry. "Can it JJ! I need a little *peace* to fix this *mess* you made last year."

I looked at Avery and snickered. We both silently mouthed: "Jay-Jay?"

Jerry was taken aback. "Woah...huh? What mess? I didn't...."

But Jasmine wouldn't be stopped. "This mess of an IO control function you wrote last year. Cripes, JJ, how many arguments can you cram into one function?"

Jerry got indignant. "Jasmine, that system works great! I didn't make a mess!"

Jasper, Jared, and Adelaide popped their heads up to listen in. This had the makings of a pretty good show.

"OH YES YOU DID!" Jasmine looked around at the silent observers. "You should see this fracking thing!"

Avery got a nasty grin on his face and started typing some commands I hadn't seen before.

"What are you doing?" I whispered.

"Watch." He replied.

And suddenly our screen filled with the contents of Jasmine's screen.

"How did you..." But Avery put his finger over his lips.

"You mean the IOTransfer function?" Jerry wheedled. "But that's just a low level..."

"THAT'S THE ONE!" Jasmine declared with triumph. "Crimeny, Jeez! What a horror scene! Whoooeee!"

Avery carefully scrolled Jasmine's screen until he found this:

int result = IOX(0x32, NO_PARITY|DMA1|CLUMP, buf, 32, 500, true);

Jerry looked around nervously. He could see that this was drawing an audience. "But Jasmine, it's just a low level IO function."

"JJ, dear..." (That got Jean's attention.) "...it's a low level IO function that you call *all over the place*! I'm having the frackingest time figuring out what each of those calls do! What possessed you?"

Avery opened an IM window and started sending the viewport address of Jasmine's screen to the observers. I could see them looking at their screens and smiling.

"Aw, come on Jasmine, we were in a hurry! We had to..."

Avery started scrolling Jasmine's screen. If she noticed we'd be toast. But she was right, calls to IOX were all over the place, and they *were* a horror scene.

if (IOX(0x38, PARITY|DMA2, s2, l, t+200, false) == 3) {

. . .

r |= IOX(0x19, VOID, startSequence, 2, -1, null);

. . .

IOX(0x18, INVERTED|NO_DMA|CLUMP, header, 16, 500, true);

I `heard` startled and strangled moans coming from nearby workstations. Apparently Avery was giving everyone the same tour of this code that I was seeing. So far neither Jasmine nor Jerry seemed to notice.

Jasmine wasn't giving any ground. "You were in a hurry? And, what? You left all this crap all over the place? "

"Well, I..."

I caught a movement from Jean out of the corner of my eye. It looked like she was about to say something. But then I saw a look come over her face that I'd never seen before. She raised an eyebrow, and then simply sat back down.

Jasmine folded her arms and continued to press her attack. "Can you please explain the arguments of this function to me, JJ? ...Can you?"

"OK, sure." Jerry started to move over to Jasmine's workstation.

"No you stay right there, JJ! I want you to *tell* me from memory. What's that first argument JJ?"

"OK, er, it's the IO address, I think."

"I think your right, JJ, very good. But do you think you could have put those blinking addresses in some nicely named variables? I mean, 0x38 doesn't mean one hell of a lot to me. What does it mean to you?"

Jerry squirmed. "Uh, yeah, uh, I think thirty-eight was the pressure transducer input."

"I think you might just be right about that JJ. I think it *is* the pressure transducer input. It only took me an hour to discover that by looking over the circuit diagrams of the module. But never mind that, can you please tell me what the PARITY and DMA and CLUMPING stuff is in the second argument?"

Little beads of perspiration were trickling down Jerry's forehead. "Uh, well, yeah. I think the second argument was a set of bits that controlled the details of the transfer. They selected things like the DMA port, and parity checking, and whether or not the data was sent byte-by-byte or in 8 byte clumps."

"And how, dear JJ, would I figure this out? It seems the only supporting document I've got is the module circuit diagram, and a chain of ECO modifications to it."

"Yeah, well, we were just in such a hurry, you know. We... Anyway, you can always ask me what these things are..." Jerry shrugged, his face was pleading for Jasmine to let him off the hook.

"Yes, you were in a *hurry*. It shows! And now *I'm* stuck with the consequences of your rushing. Thanks. But please go on. What are the rest of the arguments of this function?"

Jasmine looked at her screen. I saw her pause for half a second, and then smirk to herself.

"Why don't I just come over there and..."

Jasmine put her hands on her hips and said: "OH NO, YOU JUST TELL ME."

Jerry sighed. "OK, uh, there's the buffer address. The next is the length."

"Stop right there. Is that length in *bytes* JJ?"

"Uh, well, sometimes. That is, er, yes, it's in bytes, unless the CLUMP bit is set, and then it's in, uh, eight byte clumps."

"Nice... Go on. What comes next?"

I heard a faint echo of her words coming from nearby workstations. I glanced at Avery, and he smiled that evil smile of his. He pointed to his screen. He had set up a connection counter. There were 52 people connected to the viewport he had set up. I saw that he had also connected his microphone to the viewport, so all 52 people could hear this exchange. I knew this wasn't going to end well for us.

"Uh..." Jerry stammered. "Uh... the next argument is... uh... the timeout in milliseconds!"

Jasmine sneered, her voice dripping with sarcasm. "Really? The timeout in milliseconds? Tell me JJ *dear*, how long is minus one milliseconds?"

Jerry hung his head. "Oh, yeah, uh, yeah. We, uh, used -1 to mean wait forever."

"I see, JJ, thank you. Negative one means forever. Good. That's perfectly clear. Thanks... Now... can you tell me about that last argument please?"

"You mean the Boolean?"

"Yes, JJ, the *Boolean*."

"Uh... I think you pass true if you want the transfer to abort on error, and false if you want it to ignore errors and just finish the transfer."

Jasmine's smile was pure, cold, evil. "And what, JJ *dearest*, does it mean when you pass *null* into that Boolean argument?"

Jerry drooped like a rag doll. "Uh... null?"

"Yes, JJ, *null*."

"Oh yeah, uh... You'd pass null into the Boolean if the previous transfer aborted on error, and you wanted to continue the transfer from where it left off."

"So let me get this straight, JJ, this is a Boolean with *three* states...right?

"Well..."

"A BOOLEAN WITH THREE STATES...RIGHT?"

Jerry wouldn't look up. "Uh..."

Jasmine's voice ran cold. "Say it Jerry!"

Jerry sighed and stared at the floor. "Yes, it's a Boolean with... three states."

"Thank you JJ, that was very informative. You may sit down now."

Then Jasmine looked around the room, looked at her screen, and then looked around the room once again. "I see that Avery and Alphonse have managed to get about 60 people to watch our little show."

Avery quickly hunched his shoulders and stared at his hands. I wanted to be anywhere else except where I was.

"OK all you voyeurs, take this as a lesson. Don't make functions with lots of arguments. They're *hard* to understand. Create objects instead. Now *look at your screens*!"

Jasmine had scrolled to one of the first calls to IOX.

int result = IOX(0x32, NO_PARITY|DMA1|CLUMP, buf, 32, 500, true);

"I want all you lurkers out there to consider that Jerry *could* have written it like this:

IOChannel temperatureTransducer = new IOChannel(temperatureTransducerAddress);

temperatureTransducer.dontUseParity();

temperatureTransducer.useDMAPort(1);

temperatureTransducer.transferIn8ByteClumps();

temperatureTransducer.timeOutAfterMilliseconds(500);

temperatureTransducer.abortOnError();

temperatureTranducer.readNBytes(buf, 32)

"Then he could have used that IOChannel every time he wanted to read from the transducer."

Jasmine stood glaring around the room. Everyone else had their heads down. Jasmine's brought Mr. C's rule about function arguments up on her screen for everyone to see. Then she cranked up the volume on the viewport and laughed as everyone's speakers screamed with feedback.

**F1: Too Many Arguments**

Functions should have a small number of arguments. No argument is best, followed by one, two, and three. More than three is very questionable and should be avoided with prejudice.
