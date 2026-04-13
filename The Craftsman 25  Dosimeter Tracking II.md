# The Craftsman: 25 Dosage Tracking II Register Suit

Robert C. Martin
28 April 2004

*...Continued from last month.*

*21 Feb 2002, 0800*

The impactor that set us on our journey was 22km in diameter, and moving at 53km/sec when it slammed into the Pacific. We had launched two months earlier and were in a parking orbit 60° ahead of the Earth, waiting for the inevitable. We knew that the impact would kick up a lot of debris so we didn't want to be in near-Earth space. I guess they figured 1AU was safe.

I've seen recordings of the impact. I don't want to discuss them. Communication with parts of the Earth continued for a few weeks, but steadily diminished and then ceased. I guess it wasn't much fun down there.

That was 43 subjective years ago, in 1959. Since then we've been star-hopping; looking for a suitable home. The first ten systems we've visited haven't been very promising. There are plenty of planets, but other than the ammonia breathing dribins of α Centauri 5, we haven't seen anything even close to a biosphere.

Now we're about to plow through a cloud of molecular hydrogen at nearly C, and to protect our ouside maintenance engineers we need to rewrite an old Dosage Tracking system that Jerry and Jasper were involved with some years back.

I walked into the lab on 44 just before 0800. Carole and Jerry were already there. Jerry looked resigned and was chatting with Carole about something that he clearly found embarrassing. Their conversation broke up before I could get close enough to join. A few minutes later, Jasper, Avery, and Jean walked in together. Avery gave me a nod and a smile as though nothing were wrong. Perhaps nothing was.

I wanted to talk to him but Carole convened the meeting before I could reach him. We all started moving towards the conference table at the end of the lab. I grabbed a chair, and Avery took the one next to me. He gave me a conspiratorial nod, just as Carole started talking.

"Jean and I have written up some initial stories for the new Dosage Tracking System. By the way, we're calling it 'DTrack'. I'll walk through the stories with you, and you can ask all the questions you like. The first story is 'Register Suit'."

Carol placed an index card on the table with the words "Register Suit" written on it.

"Our system tracks the radiation dosage received by outside maintenance workers by integrating the dosages received by the space suits that they wear. Each suit has a dosimeter integrated into it's systems. When a suit is checked out for use, it's dosimeter is read before the suit is released to the worker. When the suit is checked back in the dosimeter is read again. The difference is added to the total dose for that worker.

So the first thing we need is an inventory of suits. A suit is introduced to the system with this story."

Jerry spoke up. "I supposed we're using the bar code patches that are sewn onto the suit to identify them?"

"That's right, Jerry dear." Said Jean. "As I'm sure you remember the bar code contains a six character alphanumeric string that uniquely identifies each suit."

Jerry grabbed the card and wrote 'Bar Code Patch: X(6) on it.

Jasper poked Jerry in the ribs and said: "Stop writing COBOL on the cards, Jerry."

"I can't help it." He responded. "It's just the way I think."

I asked: "So when a new suit is produced, it is given a new bar code patch and then registered with the system? How does that registration take place?"

Jerry said: "Yeah, that's right Alphonse. The new suit is tagged and then entered into the system using a bar code reader."

Carole added: "The new suit is carried to the Outside Maintenance suit-up room by hand. The clerk there selects the "Register New Suit" function on the screen and then scans the bar code. This registers the new suit with the DTrack system, and sends a message back to manufacturing telling them that the new suit is accounted for."

Jasper grabbed the card and wrote: 'Register New Suit function: on screen. Send confirmation to mfg.'

"I've never understood why we don't create a suit clearing house." Jerry said. "It doesn't make any sense for maintenance to talk to manufacturing like that."

"Later, Jerry, later." Said Carole. "I know how you feel about this issue, and I agree. But the H2 cloud is two months away and we *have* to be ready for it. Otherwise you'll be spending your shifts patching up the database corruption in that old COBOL system of yours, and I will too - and that's not the way *I* want to spend the next six to eight months."

Jerry grimaced, but nodded acceptance.

"Is that how big the cloud is; eight light-months?" asked Avery.

"That's astronometry's best guess as of last night." Replied Carole.

"Why does manufacturing need to know that the suit has been registered with DTrack?" I asked.

Jean replied: "Aphonse, dear, we track every suit from manufacturing through usage to decommissioning. When one department releases a suit another department registers it and the two departments exchange a message so that each knows what happened to the suit. That way we always know where the suits are. Can you imagine how awful it would be for the poor folks who have to use the suits if they didn't know the history of the suit, how old it was, what repairs have been done to it, what radiation exposure it has taken? Oh, I just don't want to think about it."

"What if production doesn't know about the suit that's being registered?" Avery asked.

"Production will send a denial message, and DTrack will not accept the registration." Carole replied.

I grabbed the card and wrote "Reject registration on denial" on it. Nobody seemed to mind.

"Will production send an acceptance message if they recognize the suit?"

"Yes dear." Jean responded.

Avery blurted: "What if there is no response from production?"

"Wait 10 seconds and then deny registration." Carole said.

Avery grabbed the card and wrote: '10s time out & reject'.

"What if the suit is already registered?" I asked.

"Reject the registration and do not send the confirmation message to production." Replied Carole.

I reached for the card but Avery was already writing: 'If already reg'd, reject reg & don't send conf to prod.'

"OK, one last thing." Said Carole. "Once registered, the suit should be scheduled for an inspection. This is just a flag in the database record that will prevent the suit from being checked out for use until it has been inspected."

Avery still held the card, almost as if he owned it. He scribbled: 'Sched for inspection' on it and continued to hold on to it with a proprietary demeanor.

"Are there any more questions about this story?" asked Carole. There was silence.

Jerry said: "OK, let's estimate it. Jasper, Alphonse, Avery, this story has a few complications, but it's pretty simple overall. I suggest we estimate it at four."

Jasper nodded, but I was confused. "Four what?" I asked. "Man hours?"

"No, just four." Replied Jerry. We don't assign units to these estimates; we just use them to compare one story to another. So a story that's twice as hard would be an eight. One half as hard would be a two."

Jasper reached for the card, and there was an awkward moment where it appeared that Avery would not relinquish it. But then, with a discernable grimace, he handed the card to Jasper. In the upper right corner of the card Jasper wrote the number four and drew a circle around it. Then he placed the card back on the table. Avery made a move towards it, but then thought better of it and backed off.

Carole said: "OK, now how do we test this?"

"Test it? What do you mean?" I asked.

Carole looked meaningfully at Jerry and said: "Would you care to answer your apprentice, Jerry?"

Jerry sighed and looked up at me with an air of inevitability. "Alphonse, Avery, so far you've been working on very simple systems that were designed more for your training than for actual use. DTrack is a production system, and the rules are a bit different. In a production system every requirement is specified in terms of executable acceptance tests. When the acceptance tests pass, then the requirement is done."

"Are acceptance tests like unit tests?" I asked.

"No, not at all. Acceptance tests look just like requirements. They can be read by all the stakeholders and officers. Most can write them too. Even Carole can write them." Jerry shot an evil glance towards Carole who responded by sticking out her tongue. "They are written in a system called FitNesse, which allows anyone with appropriate access to read them, write them, change them, and even execute them."

"How can they look just like requirements?" Avery asked, genuinely interested.

Carole stepped over and said: "You're about to find out. Jerry, let's write up an acceptance tests for Register Suit". And the two of them sat down at a terminal and began to type.

*To be continued...*
