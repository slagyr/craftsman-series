# The Craftsman: 56 Clean Code: C5 Commented Out Code

Robert C. Martin
25 March 2009

*May 20, 1945, 22:23:00 GMT*

*The war cabinet stared again and again at the aerial photographs that littered the conference table. "Well, that pretty much settles that; doesn't it?" Said General MacArthur.*

*"Yes it does," said Rear Admiral Souers, Chief of the newly formed Office of Intelligence. "Europe is dead, and Asia is dying."*

*"I wouldn't have believed it." Said Hoover, "But a few of our agents have reported in from Paris, and Berlin, and they confirm the assessment. Losses in Europe appear to be 70%; and Asia may go higher than that."*

*President Wallace shook his head in amazement. "Terrible." He said. "Just terrible. And yet I can't deny that there's a certain justice in all of this."*

*Patton yanked the cigar out of his mouth and said: "It would have been justice if we'd been the ones who'd done it. But these nincompoops did it to themselves. That's not justice, that's just incompetence."*

*"Do we know for sure what happened?" asked Wallace.*

*"Not really." Said Souers. "All we know is that they started contracting the Anthrax about a week before we did."*

*Patton growled: "I wager the Nazis had huge stockpiles of the stuff to use against us, and against the Japs and Bolsheviks too; once it suited their purpose."*

*"We may never know how it all got loose." Said MacArthur. "But one thing's certain."*

*"Yes." Said President Wallace. "It's just us against Clyde now."*

*Tues, 13 Mar 2002, 09:00*

I was working quietly at my screen when I noticed Jean stand up and scan the room. She saw me and beamed. "Alphonse dear, would you please come here. I've got something I'd like you to see."

That meant I was in trouble. I stood up and sauntered over to her workstation. Out of the corner of my eye I saw Avery flash me a snickering grin. He was enjoying this.

"Sit down right here, dear." Jean said as I approached her desk. She indicated to the seat next to her. So I sat down and she sat down along side.

"Now then, dear, would you please look at this screen?"

"Uh, sure." I looked at her pad, and saw the following code:

**protected void log(String message, int priority) {**

**if (project != null) {**

**project.log(message, priority);**

**}**

**// else {**

**// System.out.println(msg);**

**// }**

**}**

I'd written this code yesterday. So I knew I was in trouble for sure. "I wrote this yesterday Jean. Jerry asked me to get rid of the messages that were printing on the console after the project had been closed."

"Yes, dear, I can see that. I can also see that instead of deleting the code, you simply commented it out."

"Uh, right, I thought someone might change their mind and want those messages printed again."

Jean smiled warmly at me and said: "I see. But dear Alphonse, you do know that we Mr. C. Doesn't care for commented out code, don't you?"

Oh, oh. I'd forgotten that rule. "Oh, yeah, I forgot about that. I'll go delete it." But as a started to stand, Jean simply smiled me back into my seat.

"Don't run off just yet, dear; we should talk about this for a minute. Why did you leave that code commented out like that?"

I remembered Avery and Jean in the woodshed, and wondered if this was going to be like that. "Well, like I said, I thought someone might change their mind and want that message put back in."

"And did you think that whoever would have to put it back in would not know how to call `**System.out.println**`?"

"Uh."

"Please don't stammer dear."

"Uh... Sorry."

"So did you, dear?"

I had to backtrack the conversation to make sense of the question. "N...no, I just thought it would be more convenient for them if I left the code in there. All they'd have to do is uncomment it."

"Yes, dear, but if that's why you left that commented code in there, then why didn't you put other convenient commented code in there? After all, dear, someone might want that message written to a text file. You could have written that code and commented it out so that it would be convenient for them."

"Well, I..."

"And what if someone wanted the message converted into HTML and displayed in a drop down box that blinked the message and then faded it out? You could have put that code in there too and then commented it out for convenience."

"Jean, I..."

"Oh, dear, I'm just pulling your leg a little. But I hope you get my point. Leaving code commented out for convenience is really quite silly. There's just so much code that would be convenient to have lying around that we'd soon fill our programs with commented out code."

"I understand, Jean, I'll be more careful next time."

"I'm sure you will, Alphonse dear. But before you go, can you tell me why the `**message**` variable has a different name in the commented out code?"

I looked back and the screen. Sure enough, the commented out section used `**msg**` instead of `**message**`.

"Oh, yeah, I, uh.. I refactored the code a bit after I made the change. I thought `**message**` was a better name than `**msg**`."

"I quite agree with you dear, yes I do. But when you made that change you didn't make it in the commented out code did you?"

"No, I uh.. forgot."

"Yes, you did. And so how convenient would that be for someone who wanted to uncomment it later?"

"Yeah, I guess they'd have a little trouble until they figured out that the name had changed."

"So it wasn't really very convenient at all, was it Alphonse."

Jasmine chose that minute to stroll by. As she turned to flash me a grin, her raven hair rippled and danced around her face. "Hey there hotshot, I hear you left some code commented out."

"Jasimine dear, you mind your own business now." Jean scolded.

Jasmine snickered but never broke her stride. She disappeared around the corner.

"My but she's a feisty young lady." Jean said to the air. "But she's a very good programmer."

I waited, not knowing what to do. Jean stared contemplatively after Jasmine for a few seconds more, and then turned back to me.

"That's all Alphonse dear, you can go back to what you were working on now."

"Thank you Jean." I said as I stood. Then I walked back to my desk and sat back down.

```
That was Hysterical!!  ROTFL. Avery IMed me.
```

I turned around and rolled my eyes at him. Then I turned back and brought Mr. C's rules up on my pad.

**C5: Commented-Out Code**

It makes me crazy to see stretches of code that are commented out. Who knows how old it is? Who knows whether or not it's meaningful? Yet no one will delete it because everyone assumes someone else needs it or has plans for it.

That code sits there and rots, getting less and less relevant with every passing day. It calls functions that no longer exist. It uses variables whose names have changed. It follows conventions that are long obsolete. It pollutes the modules that contain it and distracts the people who try to read it. Commented-out code is an *abomination*.

When you see commented-out code, *delete it*! Don't worry; the source code control system still remembers it. If anyone really needs it, he or she can go back and check out a previous version. Don't suffer commented-out code to survive.
