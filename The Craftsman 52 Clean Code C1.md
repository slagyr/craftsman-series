# The Craftsman: 52 Clean Code: C1 Inappropriate Information

Robert C. Martin
20 January 2009

*December 31, 1944, 23:55:00*

*Wallace and Truman celebrated the New Year with the White House staff. Though the music was good, and the party was lively, both men kept nervously looking at their watches. If the other party-goers thought the men were steeling themselves for the inevitable speeches they'd soon be making, they were mistaken.*

*As the clock struck midnight, a new sun blossomed in the Nevada desert. Riding that fervent impulse was a vessel the size of the Lincoln Memorial. A second later, and a 100' in the air, another sun blared forth, followed by another, and then another.*

*The shock absorbers between the cabin and the pusher plate damped the 1-kiloton blasts. The twenty crewmen aboard felt the G forces rise and fall as though they were swinging high on a playground swing set.*

*They were on their way to see Clyde.*

*Thursday, 7 Mar 2002, 14:00*

The "Brown-Bag" was boring today. One of the virtual attendees gave a talk about some big framework. I didn't get the name, but I think it rhymed with "sore".

I left before it was over. Jasmine followed me out and faked stifling a yawn. We both smirked. Then she asked me if I'd mind pairing a bit with Adelaide. So after lunch I put the finishing touches on a module, made sure all the tests passed, pushed the module to github, fired off a Hudson build, and then sauntered over to where Adelaide was working.

"Hi Alphonse, thanks for stopping by." she murmured, never taking her eyes off the screen.

"Sure." I said, as I looked at her screen. She had a new module up, and it looked like she was just typing it from scratch. The only thing on the screen was:

/**

* Farvle -- a class that performs 'Farvle' operations

* on 'Arvadents' and other slithy toves.

* @Date: 7 Mar, 2002

* @Ship: Dyson

* @Guild: Mr. C

* @Author: Adelade

* Version History:

* 20020307 adl -- initial version.

*/

public class Farvle {

}

"Adelaide, why did you type all of that?"

She looked a question at me, but did not answer.

"Did Jasmine tell you to do that?"

"N-no." she stammered, "This is how they taught us to start a module in CS."

"You've been working with Jasmine for a few days now, have you ever seen anything like this at the front of the modules that you and she have worked on together?"

"N-no, I had wondered about that." She mewled. "It seemed to me that those modules weren't very well documented."

"Are you familiar with Mr. C's *Clean Code* rules?"

She hung her head and mumbled: "I haven't had time to read through them all yet."

"OK." I said. "The first rule of comments (C1) is: 'Avoid Inappropriate Information'".

She looked at me strangely for a split second, and then stared at her lap. I thought she wasn't going to respond, but a moment later she gritted her teeth and forcefully said: "I don't see anything inappropriate about that information."

I realized I needed to tread gently. After all, I was only a few days more senior than she. So I carefully held out my hands for the keyboard and said: "May I"?

She looked at me from the corner of her eye, and passed the keyboard my way.

"Thank you. Now have you created a git repository for this yet?"

"No." she said. "Not yet."

"Then please allow me." And so I saved the module and typed the commands that would create a git repository and commit her module into it.

$ git init

$ git add src/Farvle.java

$ git commit -m "initial commit"

I handed the keyboard back to her and said: "OK, now type `git log`".

She did as I asked, and up on the screen came the following message:

$ git log

commit a4c3868b8386ce5f7eeae2a449f8ef8c89081af5

Author: adelade <adelade@mrc.dyson.gld>

Date: Thu Mar 7 14:06:06 2002

initial commit

"All that information that you had put into your source file is in the git log. There's nothing inappropriate about the information itself; but the source file is the wrong place for it."

She was staring at her lap again, and I could see that she was building up to a protest. So I quickly brought the *Clean Code* rules up on the screen.

*C1: Inappropriate Information*

*It is inappropriate for a comment to hold information better held in a different kind of system such as your source code control system, your issue tracking system, or any other record-keeping system. Change histories, for example, just clutter up source files with volumes of historical and uninteresting text. In general, meta-data such as authors, last-modified-date, SPR number, and so on should not appear in comments. Comments should be reserved for technical notes about the code and design.*

She looked at the screen for several minutes, and then sat back in her seat and looked over at me.

"OK, I get it." She said. "But then why did my CS teachers tell me to put all that stuff in my source files?"

I sighed, the way I'd seen Jerry sigh when I asked him questions like that. Then I gave her the answer that he gave me: "They teach you a lot of goofy things in school, Adelade."
