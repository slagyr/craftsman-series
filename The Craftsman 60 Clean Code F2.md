# The Craftsman: 60 Clean Code: F2 Output Arguments

Robert C. Martin
20 July 2009

*Saturday, September 1, 1945, 10:45AM GMT, Caen, France.*

*Corporal Murphy and Leftenant Haynes crouched behind an overturned horse-cart that had been filled with bricks. The bricks made a reasonable bulwark against the potshots of the sniper in the second floor of the storefront across the street.*

*"Can you see him, Audie?" Haynes asked?*

*"Nah, I recon I'll have to wait for him to shoot at us again. I don't suppose you'd care to wave your rifle in the air to draw his fire?"*

*Haynes just glared at his diminutive companion. He was glad that the Americans had sent so many troops to help England fight back the remnants of the German occupation forces, and clean up the mess in Europe, but this particular Texan reminded him a bit too much of the movies about the American west he used to like to watch in the cinema.*

*The Texan picked up a brick and handed it to Haynes. "When I tell you to, toss that brick over to the right about 30 feet."*

*Haynes took the brick and watched as Murphy took aim on the building. Then Murphy said: "Go!" and Haynes tossed the brick. Murphy's rifle cracked a second after the brick hit the ground.*

*"That fella got a little too curious. I don't recon he'll be curious about anything else again. We'd best get movin'."*

*Haynes nodded, and the two of them dashed across the street and into the store. As he ran, Haynes wondered how the battle for London was going.*

*Thu, 16 Mar 2002, 11:00*

"Hay Hotshot, got a minute?"

I was just coming back from a break and happened to walk past Jasmine's workstation. I happen to walk past Jasmine's workstation a lot.

"What's up, Jasmine?"

"Look at this code. What does this mean to you?"

She had highlighted one line on her screen.

impactCount += scanImpacts(ImpactType.SMALL_PUNCTURE, impacts);

"Oh, you must be working with the micrometeoroid impact locator app. I've heard that's a real mess."

Jasmine smiled and sneered at the same time.

"Yeah, it was written about ten years ago, and it's pretty tough to work with."

I looked more closely at the line of code she had highlighted.

"OK, so it's counting the number of small punctures in the `impacts` list."

"Yeah, that's what I thought too."

"You mean that's not what it does?"

Jasmine fixed me with those gimlet green eyes and shook her head. Her long black hair shimmered and danced.

"No, that's not at all what it does."

I snapped back to reality. "Uh... it's not?"

Jasmine chuckled and said: "Dyson to Alphonse, are you there? No Hotshot, that's not what this line of code does. - Hay Jerry, got a minute?"

I'd noticed that Jerry happened to walk past Jasmine's workstation a lot too.

"I swear, Jasmine, whatever it is, I didn't do it!"

I laughed under my breath. The dressing down she had given him yesterday had been pretty funny.

"Relax, Jerry, I just want you to look at some code."

"Who wrote it? If it was me, forget it!"

"No, it wasn't you, Jerry, it was written ten years ago by Jean."

Jean stood up from her workstation. "Did someone mention my name?"

Jasmine smiled at her and said: "We're just looking at some code you wrote a long time ago, Jean."

Jean's grin and eyes got wide. "Oh my dears, I wish you lots of luck with that. Call me if you need me." And she sat back down.

Jerry had been looking at the highlighted line of code.

"This is part of that old micrometeoroid impact locator isn't it?" And then under his breath: "What a dog that system is. Anyway, it looks like this statement scans the `impacts` list for small punctures."

"That's what I thought too." I blurted. "But Jasmine says no."

Jerry looked confused. "I don't see what else it could be doing. It scan s the `impacts` list for impacts that have been classified as `ImpactType.SMALL_PUNCTURE.`

Jasmine laughed and said: "I love it when you talk to me in code, Jerry."

Jerry's mouth tightened.

Jasmine continued. "But, no, that's not what this line of code does. Not even close."

I had been looking at the rest of the code on the screen, and I noticed something.

"Jasmine, I think you're wrong. Look at the line three lines above the one you have highlighted."

Jasmine smiled evilly, and moved the highlight up.

"You mean this line, Alphonse?"

List<Impact> impacts = ImpactDB.getImpacts();

"Yeah, it's reading the impact list from the database. That makes perfect sense if the other line is scanning that list for small punctures."

Jasmine's smile got even broader. "Yes, that does make prefect sense. But that's not what's going on. Here, look at this."

Jasmine scrolled up a few more lines.

ImpactDB.setFilter(ImpactType.SMALL_PUNCTURE);

Jerry and I looked at that line for a long time.

"Confusing, isn't it?" asked Jasmine.

"Yeah." Said Jerry. "Why are they filtering on small punctures and then scanning for small punctures. That seems redundant."

I just shook my head.

Jasmine nodded and scrolled down about 20 lines. "Now look at this. Keep in mind that the filter is still set."

ImpactDB.saveImpacts(impacts);

"Yikes! This is awful!" I blurted. Then I realized that Jean probably heard me. Jerry and Jasmine just snickered.

"Yeah, it's pretty bad." Said Jasmine quietly. "So, any guess as to what that original line of code does?"

Jerry pointed at the screen. "I guess it depends on what that filter is doing."

"Yeah." Jasmine agreed. "It does."

"I don't get it." I said. "Why would you read something, scan it, and then write it. Nothing in this code changes the `impacts` list."

Jasmine looked at me out of the corner of her green eyes. "Are you sure about that Hotshot? Look again at the line I called you over here for."

My eyes scanned up the screen until I saw it.

impactCount += scanImpacts(ImpactType.SMALL_PUNCTURE, impacts);

It took about twenty seconds. Then:

"Oh! The `impacts` list is an *output*! Oh! The `scanImpacts` function gets all the impacts from some other source and *puts them into* the `impacts` list. Oh!"

Jerry rolled his eyes. "Of course. Duh; `impacts` is an output.

"Yes, dears. The `impacts` list is an output."

Jean had managed to appear right next to us while we were all staring at the screen.

She continued: "We didn't know any better in those days, my dears. We did the best we could with what we knew, but we didn't know that much. So we made really terrible errors like this one...and so many others."

"There was a time, my dears, when we weren't sure this ship would survive...oh but let's not talk about that."

The three of us were stunned. The ship's survival was never in question. Not ever. What was she talking about?

"Fortunately for us, dears, Mr. C. taught us how to write much better code. You should all go look at rule F2."

With that, Jean sauntered back to her workstation.

Jasmine smiled at us like the cat with feathers on her mouth. She silently brought up Mr. C's rules.

**F2: Output Arguments**

Output arguments are counterintuitive. Readers expect arguments to be inputs, not outputs. If your function must change the state of something, have it change the state of the object it is called on.
