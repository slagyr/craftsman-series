# The Craftsman: 57 Clean Code: E1 Multistep Build

Robert C. Martin
14 May 2009

*57 July 4, 1945, 22:23:00 GMT*

*Chuck Yeager stared out the portal at the growing giant. Clyde was still 100 kilometers away, but it filled his field of view. It was a dark grey pockmarked potato slowly rotating while hanging silently in the sky. Chuck would be one of the first to set foot on Clyde, to gather material to bring home to the egg-heads who desperately wanted to know what it was made of.*

*"They think we can move that thing." O'Hare said from behind him.*

*Yeager knew better than to show that he'd been startled. "That's what they think, alright." He responded.*

*"What do you think?" Pressed O'Hare. "Do you think we can push this thing aside?"*

*"Butch," Drawled Yeager, "I'll be damned if I know. But here's the thing."*

*"What's that, Chuck?"*

*"It's not my job to worry about that. Not yours neither."*

*"True enough. True enough. We're just supposed to walk all over that sunofabitch and pick up rocks to bring home."*

*"Sounds easy, don't it?"*

*"Piece of cake."*

*And they both turned and stared at the dark grey pockmarked potato that was rushing headlong to destroy their homes.*

*Wed, 14 Mar 2002, 08:43*

I heard an email come into my PDA while I was eating breakfast in the galley, but I ignored it until I had finished. I got up and started down the corridor to the lift that would take me to my work area. As I was walking I flipped open the PDA and started to scan the emails.

From: Jerry

Subject: PortalClean

Date: 14 Mar, 2002, 08:32

To: Alphonse

----------------------------

Alphonse,

Please check out the PortalClean system, rebuild with latest compiler, and redeploy.

Jerry.

I was reading as I turned into the lift. I walked headlong into Jasmine.

"Hey, watch where you're going there Hot Shot! Get your nose out of your PDA while you're walking!"

"Oh, sorry Jasmine."

"You *will* be sorry if you keep *that* up. Haven't you ever heard of the Discipline of D.E.?"

"Uh...what?" I hate sounding like a lunk, but the sounds come out before I can stop them.

Jasimine rolled those glorious green eyes and tossed her long black hair. "The Discipline of D.E., Alphonse. Do Easily."

"N-no I've never heard of that."

The lift engaged and we stated down towards our lab.

"The basic idea is that it's better to think about what you are doing, than it is to do one thing while thinking about another. When you are walking, think about walking. When you are eating, think about eating. You make fewer mistakes that way. Or so they tell me."

The lift door opened just as she finished.

She gave me a wink and an all-knowing grin, and strode purposefully out into the lab with her hair swinging purposefully with each stride.

I heaved a sigh and made my way to my workstation. Avery was there already.

"All set for some pairing?" He asked. "I could use a break from what Jean's got me doing. So I thought I'd help you out for a bit."

"Sure." I said with some relief. It would be good to have a bit of help with this PortalClean thing. "Jerry just sent me this email." I held my PDA out for him to see.

"Okay, that's sounds easy." He said. And while I settled myself, he checked out the project and got it up on the screen.

We both stared at the top-level directory for a few seconds. Then, in unison, we turned to look at each other, and said: "Where's the build script?" We laughed at our simultaneity, and I resisted the urge to say "Jinx".

"Okay." I said. "How do we build this thing?"

We both stared at the directory a bit longer, but all was saw were source files. There didn't seem to be any kind of script at all.

"Wow, this is strange. I wonder who wrote this?"

Avery nodded and typed: `git log`.

As the list of commits scrolled up the screen, Avery and I both sighed. Again, simultaneously we looked at each other and with as much apprehension as we could muster we said the name: "Jasper!"

Again, we laughed, although this time there wasn't as much to laugh about.

"Do you want to call him over?" Avery asked.

I paused to think about this. "No." I said. "But I think we have to."

Avery rolled his eyes in agreement, and then fired off an IM.

Avery: Jasper, got a sec?

Jasper: Sure beans! What's up Ayve ol' sport?

We looked at each other again and rolled our eyes.

Avery: Alphonse and I could use your help with PortalClean.

Jasper: PortalClean! Holy Baloney, It's been years!

Jasper: That was one of my first projects. What a hoot!

Jasper: I'll be over in a flash guys! See you in a jiffy.

Jasper: Over and out.

Jasper: BRT.

Seconds later he was there, in all his blonde, sugar-bowl haircut, toothy-smiled glory.

"Hey Alf! Hey Ayve! Good to see you guys. What's up with PortalClean? Wow, It's been a while since I've seen that code. Do you mind if I sit right here? Thanks. Yeah, that's the directory all right. So how can I help you? Have you downloaded the CleanFramework yet?"

The rush of syllables ended so abruptly it took me a moment to realize that he was done. As Jasper beamed at us with his big bright eyes and wide grin, I reviewed the deluge of words.

But Avery beat me to it. "What's CleanFramework?"

"Good question Ayve! CleanFramework is the framework <grin> that I wrote to support CleanPortal. I checked it into a separate repository because I thought someone else might be able to use it for a different cleaning application. Here, let me show you."

And Jasper grabbed the keyboard and checked out the CleanFramework project right in the same directory with CleanPortal.

Jasper stared at the screen for a few seconds and then said: "Yeah, I'm pretty sure that's right. Now, AlfAndAyve <grin> have you tried building this? Probably not since you didn't have the framework. So here's how you build it." And he began to type. "First you have to build the framework."

cd CleanFramework

javac *.java

"What are those errors?" I said, as I watched the compile fail.

"Oh yeah, that's right!" Avery exclaimed. "We need the CleanUtils library too."

And with that he checked out yet another library into the same directory.

"OK AlfAndAyve <grin> let's try that again. First we build the utils."

cd CleanUtils

javac *.java

"Next we build the Framework."

cd ../CleanFramework

javac -classpath ../CleanUtils/classes *.java

"Finally we build the application." And he beamed great big smiles at both of us.

cd ..

javac -classpath CleanUtils/classes;CleanFramework/classes *.java

"Whoops! More errors. Hmmm. Let me think."

Jasper looked up at the ceiling and put his finger on his nose. Avery and I looked at each other in horror.

"Oh yeah!" Jasper said. "I forgot to generate the data structures."

java -cp CleanUtils/classes cleanDataGenerator.Generate data/templates

... generating 152 java files from templates. (18s)

"OK, *now* I can build the application."

javac -classpath CleanUtils/classes;CleanFramework/classes *.java

I could feel the blood start to drain from my face.

"Jasper, WTF?"

"Wait Alf, hold on. We're not done."

"Not...done?"

"Now we've got to run the configuration parser."

"The...huh?"

java -cp CleanUtils/classes cleanUtils.GenConfig ../CleanFramework/config.xml

... populating configuation database. Please wait... (22s)

... Binding generated data structure to configuration entities... (14s)

... Reticulating Splines... (92s)

Avery was slowly bumping his forehead against the desk. His eyes were closed tight. His hands were over his ears.

"Wow, this machine is a lot faster than we had back when I wrote this. We usually had to wait for 10 minutes or so! OK, now let's run the tests!"

Avery and I both perked up at that. "You have tests?"

Jasper looked offended. "Alf? Ayve? What do you take me for? Of course I have tests. First we have to generate the test database."

java -cp classes TestDB.build -nl -p 32

...alternating rows with variants selected.

...populating records with mocks of type n

...Linking records and leaving 32 broken paths.

"I always left 32 broken paths. Watch what happens when I run the application."

My eyes got big. "The Application!" But I was too late. Avery slumped in his seat wracked by mock sobs.

java -cp classes CleanPortal.cleanPortal -t -v -log:null -p:Mock

******TEST MODE ON*****

Transverse access applied.

Mock Portals intialized.

(2342342)/Portal selection begins.

(2342346)/Portals selected.

(2342323)/Portal 1 - stern @ 359

(2423523)/Portal 1 - engaging washers.

(2349932)/Portal 1 - washers completed (32s)

(3930283)/Portal 1 - Optical Transmission measured (+2%)

(4342344)/Portal 1 - Clean.

(3234222)/Cleaner Clutch engaged - - Translation started.

...

I watched agog and aghast as the torrent of log messages scrolled up my screen. Jasper, with that big grin plastered on his face, stared intently at the screen watching the log messages go by. His lips were moving but I couldn't hear what he was saying. I felt uncomfortably numb.

Finally the application terminated. Jasper leaned back and said:

"Yep, I counted 32. The tests all passed."

Avery and I both stared at Jasper as though he's just confessed to murdering our mothers.

"Jasper... frack... Frack! What the *frack* was that? Holy Cecil, that was a complete Cluster Frack. What...??"

Jasper's expression changed. He looked almost like he was gloating. Then Jerry and Jasmine both popped their head up above neighboring cubicle with big condescending grins on their faces.

"Oh no!" Avery said and slouched down in his seat.

"Oh YES you Hot Shots! We *got* you!"

The three of them walked away laughing and joking with each other. Avery and I looked at each other, and then burst out laughing too. "They got us all right."

"Jinx!"

Later that day I brought up Mr. C's rules and started to read.

**E1: Build Requires More Than One Step**

Building a project should be a single trivial operation. You should not have to check many little pieces out of source code control. You should not need a sequence of arcane commands or context dependent scripts in order to build the individual elements. You should not have to search near and far for all the various little extra JARs, XML files, and other artifacts that the system requires. You *should* be able to check out the system with one simple command and then issue one other simple command to build it.
