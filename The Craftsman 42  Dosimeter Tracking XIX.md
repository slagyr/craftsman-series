# The Craftsman: 42 Dosage Tracking XIX Devious Thoughts

Robert C. Martin
2 October 2005

*...Continued from last month.*

*The High Altitude Nuclear Explosions (HANES) went off with clockwork precision. General MacArthur nodded with quiet satisfaction as he received the reports of the detonations. Better still, reports from covert agents confirmed that preparations for the invasion appeared to have ground to a halt. MacArthur eagerly anticipated the satellite photos in the coming days. He expected them to show that the Axis powers were conducting a mass withdrawal and skulking back to their bases.*

*But as the days and weeks wore on, no photos arrived. To his growing dismay, MacArthur learned that virtually every spy satellite in orbit during the HANES and those that had been launched days, and even weeks later, quickly stopped operating. Eventually he was told that it had something to do with high energy radiation belts created by the explosions.*

*General MacArthur did not like being blind.*

*22 Feb 2002, 1030*

"Jasper is driving me nuts!" I said to Jerry in the break room, once I had found a way to escape from Jasper.

Jerry smiled and nodded knowingly. "Yeah, I know what you mean. He's a bit intense isn't he?"

"A bit? I've confronted him two or three times, and yet he keeps on being condescending. It's like he wants to be my big brother or something."

"Jasper is a good programmer, Alphonse. There's a lot you can learn from him. So try to get passed his personality quirks. OK?"

"If you say so. I haven't been impressed so far. He makes me crazy!"

"Well, you've been working with him for a couple of hours today. Perhaps it's time to switch partners. If you like, I'll ask him to help Avery, while I work with you."

That sounded like a really great idea. Getting away from Jasper, even for just a few hours, had become an imperative. "I'd be in your debt!"

"OK, I'll take care of it. Besides, it should be fun to see how *those* two get along." Jerry gave me a wink; and I realized that putting Avery and Jasper together could indeed light off some fireworks.

I wandered over to a window to watch the starbow make it's lazy circular trek around our ship. The starbow was a hoop of colored stars that encircled our bow, positioned as though the ship were trying to fly through it. It's leading edge was a pale blue that grew in intensity to white at the centerline and then faded away into red. At slower speeds, the colors would fade and the starbow would widen to become the stars of the nighttime sky. I knew the effect was caused by a combination of Doppler shift, and optical aberration, but that didn't keep me from being mesmerized by it's stark beauty.

Jerry came by a few minutes later and said: "OK, it's all set. This is actually a good breaking point for Avery and I, since we just finished the story we were working on. I've asked Jasper to work with Avery on the story that the two of you were doing. You can help me write some more acceptance tests."

This was good news. I could use a break from the code; and I was tired of that Suit Registration story too. But I had a question.

"Isn't Carole supposed to be writing the acceptance tests, Jerry? After all, she's our customer, isn't she?"

"Oh, she *is* writing them. You'll see her working with Jean and I on them. But she can't write them all, so we help her."

We started walking back to the lab as we talked. Jerry was thoughtful for a second and then said:

"The normal process is for the customer, or analysts that report to the customer, to write the primary acceptance tests, and for QA to write the alternate acceptance tests."

"What's a primary acceptance test?"

"Oh, yeah, you haven't met Ivar yet. It's a term that Ivar uses to describe a test that describes the 'happy path' of a feature."

"Happy path?"

"Yeah, when everything goes right. You know, no invalid entries, no failures, no exceptions thrown, no cockpit error."

"Oh, ok, so that first Suit Registration acceptance test we did was 'Primary' because it tested that a suite would be registered if everything went OK. But the second one we were working on showed that registration failed if the suit was already registered, so it would be - er - Alternate?"

"Yeah, you got it. Anyway, QA are the folks who are trained to think of all the things that can go wrong. They explore the boundary conditions and the failure scenarios. So they usually write the 'Alternate' acceptance tests."

"Usually?"

"Yeah. Have you seen any QA folks in the lab?"

"No, it's just been you, me, Avery, Jasper, Carole, and Jean. We see Jasmine and Adelade from time to time, but they are working on SMC."

Jerry nodded. "Right. We don't have QA on our team yet. Jean's trying to get one or two QA folks assigned. Until then, it's up to us."

"OK, I see. So you and I are going to write some alternate acceptance tests?"

"Yeah, that's the plan. We'll look at the primary tests that Carole has written, and then try to imagine everything that could go wrong with them and write tests for those cases."

We walked in silence until we reached the lab, and then sat down where Jerry and Avery had been working. Jerry pulled up the FitNesse site and navigated to the `StoryDescriptions` page. It looked like this:

```
!path C:\DosageTrackingSystem\classes

^RegisterSuit
^UnRegisterSuit
^AlertManagerIfOutsideUserPastDosageLimit
^SuspendUser
^SuspendedUserAttemptsToCheckOutSuit
^AddUser
^DeleteUser
^UserDosageReport
^UserHistoryReport
^SuitHistoryReport
^SuitInventoryReport
^CheckOutSuit
^CheckInSuit
^AttemptToCheckOutSuitThatRequiresInspection
^AttemptToCheckOutSuitWhenUserOverMonthlyLimit
^SuitInspection
```

"Wow!" I said. "Somebody's been busy."

"Yeah, Carole and Jean have been entering primary acceptance tests since yesterday. They've got a lot of them done. Let's look at `UnRegisterSuit`."

Jerry clicked on the link, and the screen showed the following:

```
^UnRegisterRegisteredSuit
^UnRegisterCheckedOutSuit
```

"OK, I'll bet that first page simply unregisters a registered suit."

Was this a joke? I flashed Jerry a big Jasper grin and said: "Given the name, I'd say that's a good bet."

Jerry smirked and clicked the link.

We both examined the page for a few seconds. Then Jerry said: "OK, that's what I thought. She's loading a registered suit into the database and then unregistering it. Then she makes sure that the count of suits has been decremented, and that the appropriate message was displayed."

"Wait!" I said. "Jasper and I just wrote some of those fixture function in the last hour or so. How did Carole and Jean know to use them?"

"Didn't you tell them?"

"No, should we have?"

"Well, it would have been polite. They do have a lot of acceptance tests to write, and I'm sure they'd appreciate any help they can get."

"Uh..."

"Anyway, these tests *are* on a wiki. They must have seen what you and Jasper did, and made use of your ideas."

"Really?"

"What? Don't you think your ideas are worth using?"

"I, uh..."

"We re a team, Alphonse. We're not a group of disconnected developers. We look at each other's work and learn from it."

"OK, sure, I just didn't expect it so soon, that's all."

"So let's look at that `UnRegisterCheckedOutSuit` acceptance test. I'll bet it tries to unregister a suit that's been checked out, and makes sure the unregistration fails."

"Checked ou?"

"Yeah...being worn by someone outside the ship."

"OH! Yeah, it wouldn't be a good idea to unregister a suit that somebody was using."

Jerry grimaced in agreement and clicked on the link.

I looked it over and beat Jerry to the punch. "Yeah, she's just forcing the suit to be checked out, and then showing that unregistration fails."

"Right. OK, so what has she missed?"

"You mean on this page?"

"No, I mean, what other tests has she missed. Think deviously!"

"I, uh..." What did it mean to think deviously?

"For example, what would happen, Alphonse, if you tried to unregister a suit that was already unregistered? What *should* happen?"

"Oh! I see what you mean. Well, clearly we'd want the unregistration request to fail, and display some appropriate message."

"Good. So write the test for that!"

I thought about it for a second and then created a new page named UnRegisterUnRegisteredSuit. I smilled when I wrote that name. It thought it was clever. Jerry rolled his eyes, but remained silent. So I wrote the following.

Jerry looked it over and said: "Yeah, that looks about right." Then he looked up and called over to Carole: "Hay Carole, look at the `UnregisterUnregisteredSuit` page. Does that look right?"

Carole nodded and spend a few seconds apparently navigating to the page. Then she gave us a smile and a thumbs-up and got right back to work.

"Great." Said Jerry. "Now, Alphonse, any other devious thoughts?"

I thought for a bit and said: "No, I can't think of any other alternate tests."

"OK, then. let's look at the next story."

*The code for this article can be located at:*

```
http://www.objectmentor.com/resources/articles/CraftsmanCode/Craftsman_42_DosageTrackingSystem.zip
```

*To be continued...*

**You cannot unregister a suit that is checked out.**

If a user has checked out a suit, then we cannot allow the suit to be unregistered.

```
set suit
314159
as registered
set suit
314159
as checked out to user
Bob
check
unregister suit
314159
false
check
count of registered suits is
1
check
message
Could not unregister suit 314159. Checked out to Bob.
was printed
true
```

**Unregister a suit.**

Users can unregister a suit that has been properly registered. This will remove the suit from inventory.

```
set suit
314159
as registered
check
unregister suit
314159
true
check
count of registered suits is
0
check
message
Suit 314159 unregistered.
was printed
true
```

**You can't unregister a suit that's not already registered.**

Any attempt to do so should be ignored with an appropriate error message.

```
check
unregister suit
314159
true
check
count of registered suits is
0
check
message
Suit 314159 was not registered.
was printed
true
```
