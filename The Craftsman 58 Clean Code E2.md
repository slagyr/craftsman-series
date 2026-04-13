# The Craftsman: 58 Clean Code: E2 One Step Test

Robert C. Martin
6 June 2009

*August 11, 1945, 06:52:00*

*A brilliant hole was torn in the sky over the island. It grew with insane speed both in size and intensity, indiscriminately devouring everything in its path. Edward Teller looked out from the window of his observation plane as the tiny atoll was consumed by the power he had stolen from the core of the Sun. He watched the shock wave rip across the surface of the water and part the clouds overhead and he could envision the spherical envelope of overpressure as it expanded at many times the speed of sound.*

*No mere atomic bomb could have wrought this fearful madness and overwhelming destruction. This was an explosive that Teller called "The Super". It used a small atomic bomb merely as its trigger, causing Hydrogen to fuse into Helium, and thereby unleashing the very power of the stars on Earth.*

*"And", thought Teller, "how appropriate that we should use that power to take us to the stars."*

*Wed, 14 Mar 2002, 12:22*

After spending the morning pairing together, Avery and I went to the galley to get some lunch. After picking up our PB&Js and Hot Dogs we walked through the lunchroom and spotted Jasmine, Jean, and Jerry in the midst of an animated conversation.

Jasmine stared a Jean with eyes wide as she asked: "You mean you didn't used to write tests at all?"

"Oh my goodness, no my dear! We used to just write the code. We didn't really think about tests in those days."

"Then how did you know your code worked?" asked Jerry.

"We didn't Jerry dear. We just sort of *hoped* that it did."

"You didn't test at *all*? I yammered.

"Hello Alphonse dear. Hello Avery. Sit down boys. Sit down. We're having such a lovely discussion. Don't you boys want something better to eat than sandwiches and wienies? Growing boys like you should be eating hot meals that stick to your ribs, not this candy-food."

"We'll get a good dinner, Jean, promise. But didn't you? Test, I mean?"

"Test? Oh yes of course we did. But not like we do now. What we'd do is just sort of run the program and see if it worked. "

"But there are so many bugs you just can't find that way." Jasmine exclaimed. "You must have had a horrible defect count."

"Yes, Jasmine dear, it was certainly harder to get our programs correct. But you have to remember that our programs were much smaller in those days, and far less ambitious. Back when I started programming, a thousand lines was a big program. And back then we did something that you children don't even know about. We *desk-checked* our code."

"You what?" Avery mumbled through a mouthful of Peanut Butter and Jelly.

"We desk-checked, Avery dear. We read through our code over and over again, pretending to be the computer executing it. We'd write down the values of the variables on paper, and step through the program one line at a time."

"Without a computer?" I choked, little bits of hot-dog spluttering out of my mouth.

"Yes, dear, without a computer. It took a long time and a lot of work, but then we had nothing much better to do. You see, it took all day to compile our programs."

Jasmine nearly sprayed the drink of water she was sipping. "What? It took a *day* to run a compile?"

"Yes, dear, that's right. You see we only had one computer and..."

"ONLY ONE COMPUTER?" Several of us exclaimed at the same time.

"Yes, yes, dears. They were very expensive in those days, and so we had to share it."

"You mean you passed it from person to person?"

"Oh, no dear. The computer was locked in a huge room. Only *computer operators* could enter that room. We'd hand them our programs and they'd put them in a queue, and compile and run them for us.

"You'd *hand* them your programs?"

"That's right, Alphonse. We'd punch them onto cards, and then hand the deck of cards to the operators."

"And they'd put them in a queue?"

"Well, actually Avery dear, it was more like a big pile. They let me in the computer room from time to time and I'd see all the decks of cards lined up on a big shelf. When the computer was done with one job, the operator would pick up the next deck on the shelf and load it."

"Wow, talk about stone knives and bearskins!" Avery mumbled.

"Yes, Avery dear, I'm sure it sounds very primitive to you youngsters, but it was the cutting edge back in those days. And it got this ship built and launched."

We all sat in silence for a moment considering how much was done with so little, and how much more we have now.

"But Jean," Jasmine objected. "Desk checking couldn't have been very accurate. You must have had lots of defects. And with a 24 hour compile time, it must have taken forever to get a program done."

"Well Jasmine dear, now that you say it that way, I guess you're right. But we didn't think about it like that. We knew that a 1,000 line program was going to take several days to get working. While we were waiting for our compiles, we'd pore over the code looking for defects. We found most of them that way."

"But didn't you write any big systems? Didn't you need to have tests that you could run to make sure you hadn't broken anything?"

"Yes, Jerry dear, but that came later. In the early days it took so long to compile and run a program that nobody even considered compiling and writing tests at the same time."

"So when did that start?"

"Oh, I'd say about 20 years ago, long after we got under way. The computers had gotten fast and plentiful enough that we could spend a lot of time with them. Once we could use the computer to check our code, we stopped desk-checking."

"So what did those early tests look like? Were you doing Test Driven Development right away?"

"Oh, no dear. That didn't start until about 10 years ago."

Even Jasmine and Jerry were amazed. We all thought that these disciplines had been handed down for decades.

"What did they look like? Well dears, their wasn't any rhyme nor reason to them. They were just bits of code that we wrote to help us test our programs. Often they were nothing more than simple user interfaces for bits of our code. They didn't really test anything, they just made it easier for us to manually test them."

"Let me give you an example." Jean continued. "Once I had to write a timing queue. You'd send a job to this queue along with an integer that told the timing queue the number of milliseconds to wait before executing the job. In order to test this, I wrote a simple little console application that listened at the keyboard. Every time I hit a key it would schedule a job to echo that character 10 seconds later. I tested my queue by tapping keys to the rhythm of a song I knew. Ten seconds later the characters would reappear in the same rhythm, and I'd sing the song along with them to make sure they were showing up at the right time."

We were aghast. All of us stared at her as if she were a stranger to us. This was *Jean*! The same Jean who was such a testing fanatic. The same Jean who would call us on the carpet if we forgot a test.

"Wow." I said. Everyone else nodded.

Jasmine persisted in her questioning. "Did you run that...er...test every time you changed the system?"

Jean's face broke into a big smile. "Oh, no, Jasmine dear. Usually we just threw that test code away."

"You threw..." My mouth was hanging open. I couldn't believe what I was hearing.

"Yes, you children really are fortunate to be working today. All you boys and girls have to do is write your tests and click a little green button whenever you want to run them. And you know in seconds if the changes you just made broke anything in the system. Back then, we just didn't have the computer resources, or the understanding, to do those things.

"But then how did you learn to do TDD?"

Jean paused and hung her head for a moment. It was as if the air had suddenly chilled. Then Jean looked at me with an expression I couldn't read. It was somewhere between pride, reverence, and sadness.

"Mr. C taught us these disciplines dear; over a decade ago.

Jean's mood had changed. She clearly didn't want to continue this conversation. We all excused ourselves and went back to work. When I got back to my workstation I pulled up Mr. C's rules. And while I read, I wondered just what had happened over a decade ago.

**E2: Tests Require More than One Step**

You should be able to run *all* the unit tests with just one command. In the best case you can run all the tests by clicking on one button in your IDE. In the worst case you should be able to issue a single simple command in a shell. Being able to run all the tests is so fundamental and so important that it should be quick, easy, and obvious to do.
