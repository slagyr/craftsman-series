# The Craftsman: 61 Clean Code: F3 Flag Arguments

Robert C. Martin
19 Aug, 2009

*61: October 31, 1945. Cape Canaveral, Florida.*

*WHAM!....WHAm...WHam...Wham!....wham!...Dyson watched with a mixture of delight and terror as the distant Orion craft thrust itself into the sky. Though the nuclear detonations that drove it were small, only one kiloton each, they were still impossibly bright even through his goggles. The sound that made it through the walls of the bunker slammed into him every two seconds as the explosions hurled the ship away.*

*This was the fifth and final launch today. They joined an armada of twelve ships heading for Mars. Six of those ships would return in a year; but with luck the remaining six ships and five hundred people would stay on Mars to build a permanent base and the beginnings of a colony.*

*Freeman Dyson stared at the bright two-second flashes of light, now nearing orbit. He wondered what it would look like, three days from now, when all twelve ships simultaneously restarted their engines and left orbit for Mars. He wanted to be with them. "Next year." He said to himself. "Next year."*

*Fri, 17 Mar 2002, 11:00*

Jerry stood up and shook his fist at the ceiling. "I hate this!" He said, to no one in particular. "I fracking hate this!"

"Yikes! Jerry, what's the problem?" I asked. Usually Jerry keeps his cool, so this had to be something unusual.

Jerry looked at me. His shoulders slumped, and he let out a frustrated sigh. "I'm just such an idiot sometimes."

"I won't argue with that!" Jasmine popped up from her workstation, a mischievous grin on her face.

"Yeah, yeah, I know Jazzy, I know. Thanks for the support." He shot a quick grimace in her direction.

I pressed on. "What's the problem? What is it you hate so much?"

"Oh, Alphonse, it's just this problem I've been debugging for the last hour. The answer was right in front of me the whole time, but I just couldn't see it."

"You should have called me over to pair with you!" I said.

"Yeah Jerry!" Chided Jazmine, "The Hotshot there can fix anything."

Jerry just glared at her until she sat back down, giggling. "Yeah, I probably should have. Here, look at this."

I walked over to his workstation and saw the following code:

int status = controlRod.rotate(30, true)

"Ah! You're working on the pile control system."

"Yeah, we're changing the rotation protocol on the control rods. Some egg-head physicist has decided that we should rotate the rods thirty degrees every month instead of fifteen."

"OK." I said. "So what's the problem?"

Jerry sighed, and scrolled the screen forward a few lines. "Look at this."

int status = fuelRod.rotate(0.5, true);

"Uh, OK. So this is rotating a fuel rod by half a degree. Are these lines of code related?"

Jerry's mouth tightened. "No, but I thought they were."

"I'm not following you, Jerry."

"Yeah, I know. So here's the deal. I needed to rotate the control rods by thirty degrees. So I found this line of code that rotated the fuel rods and I figured that the control rods would work the same. So I wrote the first code I showed you."

"And it didn't work?"

"It was strange. The control rod rotated alright, but it was always about 270 degrees instead of 30."

"270? Is the rotate function broken?"

"No, I ran the tests for the rotate function over and over. I even looked at the code for the rotate function. It's all just fine."

"So what was the problem?"

Jerry looked at me sheepishly and pointed back to the screen, at the line that rotated the fuel rod by half a degree. "That's not rotating half a degree." He said.

"It's not?"

"No, it's rotating 90 degrees."

"Huh?" What was he talking about?

"Yeah, somebody got lazy. Actually two people got lazy."

"Jerry, how can rotating by .5 be 90? For that matter how can rotating by 30 be 270?"

"Actually, that 30 was a lot more than 270. It was 4.75 times around or just under 1,719 degrees."

"OK, Jerry, you are blowing my mind here. What the frack are you talking about?"

"Do you see the Boolean arguments?"

"Yeah, they're both `true`."

"Right. What do you think they mean?"

"I don't know, what?"

"Degrees or Radians."

"Huh?" I blurted.

Jerry looked at me meaningfully. "They specify whether the argument is in degrees or radians."

"OK, so `true` means degrees."

"No."

"Oh, so that was your problem! You thought the fuel rod was being rotated half a degree, and it was actually being rotated...uh...about pi over 6...about 30 degrees! Wait...you said 90."

"No, you've got the wrong idea."

"Huh?"

Jerry took a deep breath. "The rotate function for fuel rods *always* rotates the fuel rods by quarter of a turn. But it does so in half-second increments. The argument specifies the size of the increment."

"Oh, OK, so it was using half-degree increments and would complete the rotation in, uh..., half-dgree, half-second ... one degree per second... 90 seconds."

"Yes, that's right."

"OK, so `true` means degrees."

Jerry smiled, sadly. "Only half right."

"Half right?"

"Yeah, `true` means degrees for the fuel rods. But `true` means *radians* for the control rods."

"Oh! OUCH!"

"Yeah, I was rotating the control rods by 30 radians."

"Oh, so you just *assumed* that since `true` meant degrees for fuel rods, that it meant degrees for control rods too."

"Yeah, but I should't have."

"Wow, yeah, someone got lazy. They didn't use the same meaning for `true` in the rotate function."

"Actually, there never should have been a Boolean argument. There should have been two rotate functions named `rotateByDegrees` and `rotateByRadians`."

I nodded. "Yeah, that makes a lot more sense. Nobody'd get confused by that. So, who's the other guy who got lazy?"

"The guy who named the fuel rod rotate function. He should have given it a name like `incrementallyRotateQuarterTurn`.

"Yeah, I see your point. But...why were you so upset? You solved the problem. You should be happy."

"I was upset, Alphonse, because both of those lazy people were me."

I chortled at that. Shook my head, and went back to my workstation. I scanned through Mr. C.'s rules looking for the rule about flag arguments.

**F3: Flag Arguments**

Boolean arguments loudly declare that the function does more than one thing. They are confusing and should be eliminated.
