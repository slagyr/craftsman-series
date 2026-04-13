# The Craftsman: 53 Clean Code: C2 Obsolete Comment

Robert C. Martin
9 February 2009

*Jan 15, 1945, 14:22:00 GMT*

*Crewmen Yeager and O'Hare stood at the observation port and gazed out into the vast emptiness. Crewing the massive Orion vessel, as she plied her way towards Clyde, was a demanding job; so times like these were few and far between. From a distance of five million kilometers the Earth was a tiny crescent sapphire blazing amidst an impossibly starry background. The brilliant white spark of Luna nestled nearby was too small for its crescent to be resolved.*

*Chuck and Butch were racing away from home at 4km/s toward a destination many months and many millions more kilometers away. Though there were 18 other men aboard, the view out the portal left them feeling very, very alone.*

*Friday, 8 Mar 2002, 08:00*

Yesterday's session with Adelaide went pretty well. She's a bit nervous, but very smart. I sat down with her again this morning for an hour or so. We were working together on fixing a bug in an old module. As we scrolled through this module on the screen I noted a strange comment above a stretch of code.

// Slim sockets go from 9020-9025

public String getCommandLine() {

return slimCommand;

}

public int getNextSlimSocket() {

synchronized (slimSocketOffset) {

int base = slimSocketOffset.get();

base++;

if (base >= 10)

base = 0;

slimSocketOffset.set(base);

return base + 8085;

}

}

Adelaide scrolled right past that code, so I motioned for her to stop.

"Did you see that?" I asked.

She kept her eyes on the screen and said: "See what?"

"That comment."

"What comment?"

"You didn't see it?"

She hung her head a little and then looked over at me. "I usually don't bother to read the comments when I'm skimming the code. " She said.

"Yeah," I said. "What good are they if you don't read them? Scroll back a little."

She winced, but waved her hand at the screen and the code scrolled back to where the comment was.

"Oh!" She said. "The comment is wrong! The ports go from 8085 to 8095."

I looked at her and said: "Not only that, it's in the wrong place. It looks like someone plopped that `getCommandLine()` function between the comment and the `getNextSlimSocket`() function. I wonder how old that comment is. Someone must have changed the code after the comment was written." I looked at her pointedly and said: "Someone who didn't read comments."

She looked at me crossly, but then softened.

"We could look in the git logs" She offered "and see who did this and when."

"There's no point." I said. And I grabbed the keyboard and deleted the comment.

"Wait! Why'd you do that?" She blurted.

"The comment wasn't just wrong and misplaced," I said. "It was worthless. The code tells us what's really going on, and it's pretty clear."

She looked at me skeptically. "Why do you think it's worthless? We can make it right."

"Look," I said. "This code was modified at least twice, and both times the modifier ignored the comment. In once case they separated the comment from the function it described. In the second case they changed the algorithm to disagree with the comment. Since nobody apparently bothered to read the comment, the comment must be useless."

She shook her head and said: "I don't get that. Once we fix it the comment will be right, and will help other people who read through this module."

"Like it helped you?" I prodded? "People don't tend to read comment unless they are stuck. If the code is understandable, like this code is, then they'll just ignore the comment. They'll assume they know what it says."

She scrunched up her shoulders and stared at the screen.

"Fine." She said.

I sighed and brought up Clean Code rule C2 on the screen.

***C2: Obsolete Comment***

*A comment that has gotten old, irrelevant, and incorrect is obsolete. Comments get old quickly. It is best not to write a comment that will become obsolete. If you find an obsolete comment, it is best to update it or get rid of it as quickly as possible. Obsolete comments tend to migrate away from the code they once described. They become floating islands of irrelevance and misdirection in the code.*

"See?" I said. "It's part of Mr. C's rules."

"He doesn't say to delete it, he says to update it."

"Actually, he says not to write a comment that will become obsolete. Since this comment became obsolete, it shouldn't have been written. So I deleted it."

"You just don't like comments." She pouted.

I wasn't getting anywhere this way. I was about to shrug my shoulders and move on when I notice that Jean had moved in behind us and had been following the whole conversation.

"Goodness, Gracious, what a fuss!" She coddled. "Dear Adelaide it's good that you want to help other people read your code. And comments can be a powerful way to help others. But, my dear, I'm sorry to say that Alphonse is right in this case. He's such a fine young developer, just like you are dear. You see, a comment's true value is to clarify something that's unclear. If that thing is clear already, like the code you two have been bickering over, then the comment serves no good purpose and should be removed."

Adelaide hung her head and nodded, but kept her eyes on her hands.

"Alphonse, dear," Jean said. "I'm sure you've got lots of your own work to do. I'd like to spend some time working with Adelaide now. Why don't you find Avery and get him to help you?"

I thanked Jean and Adelaide, and wandered back to my workstation.

"What was that all about?" I asked myself.
