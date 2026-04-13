# The Craftsman: 62 The Dark Path

Robert C. Martin
3 Oct, 2010

*Fri, 17 Mar 2002, 14:00***

"Hey Alphonse," Jerry called as I walked by, "let's do a bit of practice. I've got a kata I'd like to show you."



I felt I could use the break so I walked over and sat next to Jerry.



"Sure, Jerry, what's a Kata?"



Jerry rolled his eyes. "You've never done a kata?"



I could feel my guard going up, but I tried to relax. "No, can't say I have."



Jerry smirked and then called over to Jasmine: "Hay Jaz, do you want to tell Alphonse what a kata is?"



Jasmine's long dark hair swished playfully as she turned her head to face me. She nailed me with those sparkling green eyes as she answered: "What, the hotshot's never done a kata?"



"He says not. Can you believe it?"



"Jeez, what do they teach these kids nowadays?"



"Oh come on!" I said, starting to get annoyed. "You guys are only a couple of years older than me, school hasn't changed that much."



Jasmine smiled at me, and I felt my annoyance evaporate. That smile... "Relax Alphonse, we're just poking fun at you. A kata is just a simple program that you write over and over again as a way to practice. We do them all the time. It's part of our normal discipline."



"You write the same code over and over?" This was new to me, and it didn't make a lot of sense.



Jerry nodded and explained: "Yeah. Sometimes we'll do a kata two are three times in a row, exactly the same each time. It's a good way to practice your hot-keys."



"And sometimes," Jasmine added, "we solve them in different ways, using different techniques as a way to learn different approaches and reinforce our disciplines."



"And sometimes we just do them for fun." Jerry concluded.



"Which one are you going to show him?" Jasmine asked.



"I was thinking about doing 'Word Wrap'."



"Oh, that's a good one. You're going to like this Alphonse. You guys have fun." And with that she turned back to her work.



I turned to Jerry and asked: "Word Wrap?"



"Yeah, it's a simple problem to understand, but it's oddly difficult to solve. The basic premise is really simple. You write a class called Wrapper, that has a single static function named wrap that takes two arguments, a string, and a column number. The function returns the string, but with line breaks inserted at just the right places to make sure that no line is longer than the column number. You try to break lines at word boundaries."



I thought about this for a moment and then said: "You mean like a word processor, right? You break the line by replacing the last space in a line with a newline."



Jerry nodded. "Yeah that's the idea. Pretty simple huh?"



I shrugged. "Sounds simple, yes."



Jerry pushed the keyboard in my direction. "OK, then why don't you start."



I knew this was a trap of some kind, but I couldn't see how. So I said: "I suppose we should begin with simple degenerate tests." I took the keyboard and began to type. I got the first few tests passing as Jerry watched.

```java
@RunWith(Suite.class)
@Suite.SuiteClasses({
  WrapperTest.DegenerateTests.class})

public class WrapperTest {
  public static class DegenerateTests {
    @Test
    public void emptyString() throws Exception {
      assertThat(wrap("", 1), equalTo(""));
    }

    @Test
    public void stringShorterThanCol() throws Exception {
      assertThat(wrap("this", 10), equalTo("this"));
    }
  }
}
public class Wrapper {
  public static String wrap(String s, int col) {
     return s;
  }
}
```

Jerry got real interested as I wrote this. When I got the second test working he said: "What's all that `**@RunWith**` and `**@Suite**` stuff you are typing?"



I smiled. Apparently I was about to teach Jerry something. "Oh, yeah." I said nonchalantly. "That's the *TestNest* pattern. I learned it from Justin a few days ago. It lets you put more than one test class in a file. Each test class can have it's own setups and teardowns."



"Yeah, that's kind of slick. But who's this Justin dude?"



I pointed and counted ceiling lights. "He works just down the hall, beneath the 8th light."?



"You mean by those guys who are always walking on treadmills while they code?"



I nodded and kept on coding while Jerry stared back down the hall and recounted the lights.

```java
@RunWith(Suite.class)
@Suite.SuiteClasses({
  WrapperTest.DegenerateTests.class,
  WrapperTest.wrapWordsTest.class
})

public class WrapperTest {
  public static class DegenerateTests {
    @Test
    public void emptyString() throws Exception {
      assertThat(wrap("", 1), equalTo(""));
    }

    @Test
    public void stringShorterThanCol() throws Exception {
      assertThat(wrap("this", 10), equalTo("this"));
    }
  }

  public static class wrapWordsTest {
    @Test
    public void wrapTwoWordsAfterSpace() throws Exception {
      assertThat(wrap("word word", 6), equalTo("word\nword"));
    }

    @Test
    public void wrapThreeWordsAfterFirstSpace() throws Exception {
      assertThat(wrap("word word word", 6), equalTo("word\nword\nword"));
    }
  }
}
public class Wrapper {
  public static String wrap(String s, int col) {
   if (s.length() <= col)
     return s;
    else
     return s.replaceAll(" ", "\n");
  }
}
```

Jerry looked back just in time to see that last test pass. He looked over the code and nodded. "Yes, that's just about exactly how I solved it the first time. That `**replaceAll**` is a bit of a hack isn't it."



"Yes, but it gets the test to pass. 'First make it work, then make it right.'"



Jerry nodded.



"Anyway, it's pretty straightforward so far." I said. And so I went on to write the next test.

```java
    @Test
    public void wrapThreeWordsAfterSecondSpace() throws Exception {
      assertThat(wrap("word word word", 11), equalTo("word word\nword"));
    }
java.lang.AssertionError:
Expected: "word word\nword"
     got: "word\nword\nword"
```

Jerry nodded sagely. "Yes, that's the obvious next test."



"Yes, and with the obvious failure." I agreed. So then I looked back at the code.



I stared at it for a long time. But there did not seem to be any simple thing that I could do to make the test pass.



After a few minutes, Jerry said: "What's the matter Alphonse? Stuck?"



"No, this should be simple. I just..." In frustration I took the keyboard and began to type. I typed for quite a while, adding and erasing code. Jerry nodded knowingly, and sometimes grunted. After about five minutes Jerry stopped me. The code looked like this:

```java
public class Wrapper {
  public static String wrap(String s, int col) {
    if (s.length() <= col)
      return s;
    else {
      int lastSpace = 0;
      int space;
      while ((space = s.indexOf(" ", lastSpace)) != -1) {
        if (space > col) {
          s = s.substring(0, lastSpace) + "\n" + s.substring(lastSpace+1);
          //todo this doesn't look right.
        }
        lastSpace = space;
      }
      return s; // really?
    }

//     return s.replaceAll(" ", "\n");
  }
}
```

"Are you sure you're on the right track, Alphonse?"

I looked at the code and realized that I had been coding blindly. I ran the tests in desperation, but they just hung in an infinite loop. I could *kind of feel* what needed to be done, but it wasn't at all clear how I should proceed.

"Give me another shot at this." I said, as I erased all the code and started over. Jerry just smiled and watched as I flailed around for another five minutes or so. Finally, with lots of tests failing he stopped me again.

```java
public class Wrapper {
  public static String wrap(String s, int col) {
    if (s.length() <= col)
      return s;
    else {
      StringBuilder builder = new StringBuilder();
      String[] strings = s.split(" ");
      int column = 0;
      for (String segment : strings) {
        column += segment.length();
        if (column < col)
          builder.append(segment+" ");
        else
          builder.append(segment+"\n");
      }
      return builder.toString();
    }

//     return s.replaceAll(" ", "\n");
  }
}
```

"What are you doing wrong, Alphonse?"



I stared at the screen for a minute. Then I said: "I *know* I can get this working, give me another shot."



"Alphonse, I *know* you can get it working too; but that's not the point. Stop for a minute and tell me what you are doing wrong."



I could hear Jasmine stifling a giggle. I looked over at her, but she didn't meet my eye. Then I took my fingers off the keyboard and hung my head. "I can't seem to get this test to pass without writing a lot of untested code." I said.



"That's true." Said Jerry, but it's not quite the answer I was looking for. You were doing something wrong. Something *really* wrong. Do you know what it was?



I thought about it for awhile. I had been trying to figure out the algorithm. I had tried lots of different approaches. But all my guesses turned out wrong. -- Oh!



I looked Jerry square in the eye and said: "I was guessing."



"Right!" Jerry beamed. "And why were you guessing?"



"Because the test was failing and I couldn't figure out how to get it to pass."



Now Jerry narrowed his gaze, almost like he was looking *through* me. "And what does that tell you?"



"That the problem is hard?" I guessed.



"No, Alphonse, the problem is *not* hard. When you see the solution, you're going to be very angry at yourself. The reason you could not figure out how to pass that test, Alphonse, is that you were trying to pass the *wrong* test."



I looked at the tests again. They seemed perfectly logical. So I asked Jerry: "How could these be the wrong tests?"



Jerry smiled with a grin that rivaled Jasper's. "They are the wrong tests, Alphonse, because you could not figure out how to pass them."



I gave Jerry a stern look. "You're being circular, Jerry."



"Perhaps I am. Look at it this way. The test you are trying to pass is forcing you to solve a very large part of the problem. Indeed, it might just be the *whole* problem. In any case, the bite you are taking is too big."



"Yeah, but..."



Jerry stopped me and said: "Did you ever read *The Moon is a Harsh Mistress* Alphonse?"



"Uh... Heinlein, wasn't it? Yes, I read it a few years back. It was a great story."



"Indeed it was. Do you remember this quotation?"

"[W]hen faced with a problem you do not understand, do any part of it you do understand, then look at it again."

"As a matter of fact, I do. I thought it was very profound."



"OK then Alphonse, apply that here. Find some part of this problem that you *do* understand."



"I understand the problem..."



"No, you *think* you understand the problem, but clearly you don't. If you understood it, you'd be able to solve it. Find some simpler tests to pass."



I thought about this for a few seconds. What was so hard about this problem? The thing I'd been struggling with was how to deal with breaking the lines at spaces? Each of my "solutions" was tangled up with hunting for just the right space to replace with a line end.



I looked at Jerry and said: "What if I solved the part of this problem that did not deal with spaces? Lines that have no spaces only need to be broken once they've hit the column limit."



Jerry pointed at the keyboard, and I started again. I wrote the same degenerate tests.

```java
  public static class DegenerateTests {
    @Test
    public void wrap_EmptyString_ShouldBeEmpty() throws Exception {
      assertThat(wrap("", 1), equalTo(""));
    }

    @Test
    public void stringShorterThanColDoesNotWrap() throws Exception {
      assertThat(wrap("word", 10), equalTo("word"));
    }
  }
public class Wrapper {
  public static String wrap(String s, int col) {
      return s;
  }
}
```

But then I changed tack and wrote a test that wrapped a line without spaces. That test was trivially easy to pass.

```java
  public static class SplitWordTests {
    @Test
    public void splitOneWord() throws Exception {
      assertThat(wrap("word", 2), equalTo("wo\nrd"));
    }
  }
public class Wrapper {
  public static String wrap(String s, int col) {
    if (s.length() <= col)
      return s;
    else
      return (s.substring(0, col) + "\n" + s.substring(col));
  }
}
```

The next test was pretty obvious. It should continue to wrap a string without spaces, creating lines that are no longer than the column limit.

```java
    @Test
    public void splitOneWordManyTimes() throws Exception {
      assertThat(wrap("abcdefghij", 3), equalTo("abc\ndef\nghi\nj"));
    }
```

Jerry looked at the test and nodded. "How will you solve that, Alphonse?"



"I just need to put a loop into the wrap function." I said.



"I think there's an easier way." He said.



I looked at the code for a bit, and then said: "Oh! Sure, I could recurse."

```java
public class Wrapper {
  public static String wrap(String s, int col) {
    if (s.length() <= col)
      return s;
    else
      return (s.substring(0, col) + "\n" + wrap(s.substring(col), col));
  }
}
```

The tests passed, and Jerry nodded approvingly. "That looks like a framework you could build upon. What's next?"



"Now that I can wrap lines *without* spaces, it ought to be easier to wrap lines *with* spaces!"



"Give it a shot." He said. So I wrote the simplest test I could. A space right at the column limit.

```java
  public static class WrapTwoWords {
    @Test
    public void wrapOnWordBoundary() throws Exception {
      assertThat(wrap("word word", 5), equalTo("word\nword"));
    }
  }
```

"Do you remember how you made that test pass last time?" Jerry asked.



"Yeah." I grimaced. "I use the `**replaceAll**` hack."



"Is that how you're going to solve it now?"



I looked at the code, and the answer was obvious. "Of course not!" I exclaimed. "All I need to do is check to see if the character at the column limit is a space!" and I wrote the following code.

```java
public class Wrapper {
  public static String wrap(String s, int col) {
    if (s.length() <= col)
      return s;
    else if (s.charAt(col-1) == ' ')
      return (s.substring(0, col-1) + "\n" + wrap(s.substring(col), col));
    else
      return (s.substring(0, col) + "\n" + wrap(s.substring(col), col));
  }
}
```

"Why'd you put that wrap call in there?" Jerry asked. "You're getting a little ahead of yourself, aren't you?"



"I guess, but it's kind of *obvious* that it belongs there. Just look at the symmetry!"



"I agree." Jerry said smiling. "Continue on."



The next test was just as obvious. The space should be *before* the column limit. So I typed the following:

```java
    @Test
    public void wrapAfterWordBoundary() throws Exception {
      assertThat(wrap("word word", 6), equalTo("word\nword"));
    }
```

"Passing this one is going to be tricky." I said under my breath.



"Is it?" Jerry queried.



I looked again, and it hit me. "Oh, no, it's just a small change!" And I typed the following.

```java
public class Wrapper {
  public static String wrap(String s, int col) {
    if (s.length() <= col)
      return s;
    int space = (s.substring(0, col).lastIndexOf(' '));
    if (space != -1)
      return (s.substring(0, space) + "\n" + wrap(s.substring(space+1), col));
    else
      return (s.substring(0, col) + "\n" + wrap(s.substring(col), col));
  }
}
```

The tests passed, and I was getting excited. "This is so strange, the whole algorithm is just falling into place!"



"When you choose the right tests, Alphonse, they usually do."



"OK, so now let's make the column boundary really small so that it has to chop the string up into lots of little lines."

```java
    @Test
    public void wrapWellBeforeWordBoundary() throws Exception {
      assertThat(wrap("word word", 3), equalTo("wor\nd\nwor\nd"));
    }
```

"That one passes right away!" I said. Wow, I think we're done.



"Not quite." Jerry said. "There's another case."



I studied the tests. "Oh, there's the case where the character *after* the column limit is a space." I wrote the tests, and it was trivial to pass.

```java
    @Test
    public void wrapJustBeforeWordBoundary() throws Exception {
      assertThat(wrap("word word", 4), equalTo("word\nword"));
    }
public class Wrapper {
  public static String wrap(String s, int col) {
    if (s.length() <= col)
      return s;
    int space = (s.substring(0, col).lastIndexOf(' '));
    if (space != -1)
      return (s.substring(0, space) + "\n" + wrap(s.substring(space + 1), col));
    else if (s.charAt(col) == ' ')
      return (s.substring(0, col) + "\n" + wrap(s.substring(col + 1), col));
    else
      return (s.substring(0, col) + "\n" + wrap(s.substring(col), col));
  }
}
```

Jerry smiled as the tests passed. "That's the algorithm all right. But I bet you could clean this up a bit."



"Yeah, there *is* a lot of duplication in there." So I cleaned up my work with the following result.

```java
public class Wrapper {
  public static String wrap(String s, int col) {
    return new Wrapper(col).wrap(s);
  }

  private int col;

  private Wrapper(int col) {
    this.col = col;
  }

  private String wrap(String s) {
    if (s.length() <= col)
      return s;
    int space = (s.substring(0, col).lastIndexOf(' '));
    if (space != -1)
      return breakLine(s, space, 1);
    else if (s.charAt(col) == ' ')
      return breakLine(s, col, 1);
    else
      return breakLine(s, col, 0);
  }

  private String breakLine(String s, int pos, int gap) {
    return s.substring(0, pos) + "\n" + wrap(s.substring(pos + gap), col);
  }
}
```

I looked at the code in some astonishment. This really was a very simple algorithm! Why couldn't I see it before?



"You were right." I said to Jerry. "Now that I see this algorithm for what it is, it's kind of obvious. I guess choosing the right tests is pretty important."



"It's not so much choosing the *right* tests, Alphonse; it's about realizing that you are trying to solve the *wrong* test."



"Yeah, the next time I get stuck like that, and start guessing and flailing, I'm going to re-evaluate the tests. Perhaps there'll be simpler tests that will give me a clue about the real solution."



And then I stopped myself and asked: "Is that true, Jerry? Is there always a simpler test that'll get me unstuck?"



Jerry was about to answer when a spitwad hit him in the side of the face. Jasmine was laughing and running down the hall. Jerry lept out of his seat to chase after her.



I just shook my head and wondered.
