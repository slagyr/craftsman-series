# The Craftsman: 26 Dosage Tracking III A Tabled Requirement.

Robert C. Martin
11 May 2004

*...Continued from last month.*

*21 Feb 2002, 0900*

Our ship, the *Dyson*, can accelerate at 1G for 60 months without refueling. During the first year of an interstellar trip we accelerate to nearly C. During the last year we decelerate to the reference frame of our destination. At speeds close to C the rest of the universe is subjectively so small that we can go just about anywhere in a few weeks or months. Theoretically that means we could go just about anywhere in the universe in two subjective years.

In the last 43 years we've stopped at ten stellar systems. We spend a year or so at each one, exploring, refueling and refitting. Then it's off to the next - in search of a home for the million frozen embryos on board. Usually we stop accelerating at about .999C, and coast for several months as a way to conserve fuel. There have been two stellar systems that did not provide us with enough Uranium to adequately refuel, and the captain isn't the kind of person to trade time for risk.

Right now we are at peak velocity, and have been coasting for a few months. We've got another eight months to go before we've crossed the 20 light-years of this jump, and start to decelerate. So we're going to plow through that molecular hydrogen cloud in front of us at a screaming clip.

Avery and I stood directly behind Jerry and Carole as they began to work on the acceptance test for the *Register Suit* story. Jerry opened a network browser and went to a site named FitNesse. He did some things on the screen that I didn't follow but quickly wound up at a page whose name appeared to be *RegisterNormalSuit*.

"OK." He said. "Let's describe what normally happens when we add a suit.".

"What do you mean by normal?" asked Avery.

Carole said: "Nothing goes wrong. Everything works as it is supposed to."

"Right." Said Jerry. "So first we want to assert that the current suit inventory is empty."

"What's normal about that?" Avery sneered.

"Avery dear," Jean said soothingly, "we start by making the simplest assumptions. Don't worry, we'll also specify the cases where the database is not empty. Right now, though, it's just easier to assume that there aren't any suits in the database."

Jerry typed something and the following table appeared on the screen.

```
Suit inventory parameters
Number of suits?
0
```

"What's that?" I asked.

"It's an assertion." Jerry responded. "I am asserting that there are no suits in the database. Next I want to assert that there is a request to register a new suit."

He continued to type, and another table appeared right below the first:

```
Suite Registration Request
bar code
314159
```

"I'm confused." I said. "Why is there a question mark in the first table, but not in the second?"

Jasper walked over and gave me a big toothy grin. "Woah, Jerry, you've got a bright one here. Good eye, Alphonse!"

Out of the corner of my eye I could see Avery stiffen. He didn't like Jasper complimenting me.

"The reason" said Jerry, "is that the first table is a query, whereas the second is a statement. In the first table we are asking the system how many suits are in the database. In the second table we are telling the system that suit number 314159 is being registered."

"That's right." blurted Avery quickly, "you see, Alphonse, the first table can be checked, but the second table is just a fact."

Jasper's eyebrows shot up. "Very *good* Avery! Gosh, Jerry, I think we've got two keepers here."

Avery stood next to me, smiling, but I could tell he was feeling superior.

"Wait." I said. "What do you mean the first table can be checked? That doesn't make a lot of sense to me."

Avery started to answer but couldn't seem to find the words. "Uh, well, the uh..."

Jerry stepped in and rescued him. "FitNesse is going to execute these tables." He said. "When it executes the first table it will check to be sure that the number of suits is zero. That's what the question mark tells FitNesse to do. If the number of suits is not zero, then that cell in the table will turn red, otherwise it will turn green."

Avery blinked, but didn't say anything. I, on the other hand, forged right on ahead. "You mean the cells change color?"

"Right." Said Jerry. He pointed to the screen that held the two tables. "Do you see the 'Test' button on the screen? When I push it, FitNesse will read the tables one by one. For each table it will pass some data into the DTrack system, and read some other data out. The question mark is for data that comes *out* of DTrack. If the data coming out matches the data in the table, then FitNesse turns the cell green. Otherwise it turns it red."

Avery muscled in again. "I see! So the first table *asks* DTrack how many rows are in the suit database, and will turn red if any number other than zero is returned. The second table *tells* DTrack to register suit 314159."

"That's about it." Said Jasper Jovially.

"Let's finish this test." Said Carole impatiently.

Jerry turned back to the console. "OK, so next we want to make sure that the appropriate message gets sent to manufacturing."

```
Message sent to manufacturing
message id?
message argument?
message sender?
Suit Registration
314159
Outside Maintenance
```

Avery looked puzzled. "So when FitNesse executes this table, will it ask Manufacturing what messages it received?"

"No," said Jasper. "We'll catch the message before it goes." Then he looked at Carole mischievously, and said "It would sure burn Courtney's breeches if we brought her system down by sending wild messages every time we tested, eh Carole?"

Carole rolled her eyes and said "Let's try to focus here. What's next?"

I said: "I guess we need to assert that manufacturing sends back an acceptance message."

"Right you are, Alphonse." Said Jerry as he typed in the appropriate table.

```
Message received from manufacturing
message id
message argument
message sender
message recipient
Suit Registration Accepted
314159
Manufacturing
Outside Maintenance
```

"You forgot the question marks." Avery said. You could tell he was pleased to catch Jerry in an error.

"Did I?" Jerry replied.

Avery looked uncomfortable. I thought I knew why Jerry had left the question marks off, but I held my peace.

Carole said: "No question marks are necessary because we are telling the DTrack system that Manufacturing sent this message. We aren't asking." Carole's patience was clearly wearing thin. She wanted to get this done. "What's next Jerry?"

"OK, having received that message, the DTrack system should enter the suit into the database. So now the database should have the suit in it."

```
Suits in inventory
bar code
next inspection date
314159
2/21/2002
```

"Why did you put today's date as the inspection date?" I asked.

Avery said: "Because, Alphonse, according to Carole's story, newly registered suits have to be scheduled for inspection."

"Yes, I remember that;" I replied, "but why *today's* date? The test won't work if we run it tomorrow. Are we going to have to change that date every day that we run this test?"

Jasper quipped "That's your job, Jerry. We want you to come in every morning and change the date."

"No thanks" Said Jerry. "No, we need to specify 'today's' date in the test. So let's do that as the first table."

```
DTrack Context
Today's date
2/21/2002
```

"OK." Said Carole; still trying to move things along. "I think that's the story. Now let's dress it up a little." She took the keyboard and began to write words around the tables. When she was done, the page looked like this:

**Normal suit registration.**

*We assume that today is 2/21/2002.*

```
DTrack Context
Today's date
2/21/2002
```

*We also assume that there are no suits in inventory.*

```
Suit inventory parameters
Number of suits?
0
```

*We register suit 314159.*

```
Suit Registration Request
bar code
314159
```

*DTrack sends the registration confirmation to Manufacturing.*

```
Message sent to manufacturing
message id
message argument
message sender
Suit Registration
314159
Outside Maintenance
```

*Manufacturing accepts the confirmation.*

```
Message received from manufacturing
message id?
message argument?
message sender?
message recipient?
Suit Registration Accepted
314159
Manufacturing
Outside Maintenance
```

*And now the suit is in inventory, and is scheduled for immediate inspection*

```
Suits in inventory
bar code?
next inspection date?
314159
2/21/2002
```

"Great!" Said Carole. "A very nice requirement."

I had to admit, it was pretty clear. But there was something I still didn't understand.

"How do you get FitNesse to execute those tables?" I asked.

Jerry looked up and said: "Sit down, Alphonse; you too Avery; lets make this requirement turn red!"

*To be continued...*

http://fitnesse.org
