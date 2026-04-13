# The Craftsman: 63 Specifics and Generics

Robert C. Martin
8 Nov, 2010

*Sat, 18 Mar 2002, 13:00*

I was sitting in the observation deck practicing some code katas when I saw Avery walk by.

"Avery, come take a look at this."

Aver stopped and looked over my shoulder. "Ah, you're doing the *Stack* kata."

"Yes, this is the third time I've done it this morning, and I've noticed something interesting. Do you have a few minutes?"

Avery sat down next to me and chortled: "Most certainly and without the slightest hesitation."

I didn't want to get into the formal banter game that we often played, so I just said: "Do you remember one of Mr. C's rules about TDD? The one about tests being specific and code being generic?"

"Indeed I do, Alphonse. Indeed I do. Let's see. (Ahem): '*As the tests get more specific, the code get's more generic*.' Is that the one?"

"Yes, that's the one. I always thought I knew what it meant, but this morning something kind of hit me."

"And what was it that caught your attention, if I may ask?"

"Let's walk through the *Stack* kata, and I'll show you."

"Very well, Alphonse, Shall I endeavor to write the first test?"

"Please do."

And so Avery wrote the first test of the *Stack* kata:

public class StackTest {

@Test

public void newStackShouldBeEmpty() {

Stack stack = new Stack();

assertThat(stack.size(), equalTo(0));

}

}

I responded with the standard ritual. "OK, now I can make that fail with this..."

public class Stack {

public int size() {

return -1;

}

}

"...and I can make it pass with this."

public int size() {

return 0;

}

"Excellent, Alphonse. Nicely done! Now for the next test, we'll ensure that the size, after a push, is one."

@Test

public void sizeAfterPushShouldBeOne() {

Stack stack = new Stack();

stack.push(0);

assertThat(stack.size(), equalTo(1));

}

"OK, and I can make that pass with a simple increment."

public class Stack {

private int size;

public int size() {

return `**size**`;

}

public void push(int element) {

size++;

}

}

"Oh, well done, well done, Avery old chap..."

I interrupted him. "OK, Avery, this is what I wanted to show you. Notice that I changed the size method to return a variable instead of a constant?"

"Indeed!"

"Right, so that's taking something very specific and making it more general."

"Indeed. Indeed. What could be more specific than a constant? And certainly a variable, by its very nature, is more general than a constant. After all a constant has only one value, whereas a variable can have many different values. An astute observation that!"

"But now," he continued, "I must insist that after a pop, the size should be zero once again."

Had he really understood my point? If not, I had more to show him.

"Right." I said. "And don't forget to refactor the tests to eliminate the duplication."

"Oh, I shan't forget, Alphonse. I shan't forget."

public class StackTest {

private Stack stack;

@Before

public void setUp() {

stack = new Stack();

}

...

@Test

public void sizeAfterPushAndPopShouldBeZero() {

stack.push(0);

stack.pop();

assertThat(stack.size(), equalTo(0));

}

}

"OK, and now I can make this pass with a simple decrement."

public int pop() {

size--;

return -1;

}

"Capital, old sport! Capital! But now, I'm afraid that it's quite necessary to prevent you from popping an empty stack. I do hope that doesn't inconvenience you too much."

@Test(expected=Stack.Underflow.class)

public void shouldThrowUnderflowWhenEmptyStackIsPopped() {

stack.pop();

}

"Good." I said, still ignoring his patois, "Now I can make that pass by checking for zero."

public int pop() {

if (size == 0)

throw new Underflow();

size--;

return -1;

}

...

public class Underflow extends RuntimeException {

}

"Outstanding! Simply outstanding! You clearly have mastery over your medium. But now, alas, I must further impose upon you. You see, you must not allow the stack to exceed the specified size."

@Before

public void setUp() {

stack = new Stack(`**2**`);

}

...

@Test(expected=Stack.Overflow.class)

public void shouldThrowOverflowWhenFullStackIsPushed() {

stack.push(1);

stack.push(2);

stack.push(3);

}

"Good. Now I can make that pass by creating the constructor and then comparing the size against the capacity in the push method.

public class Stack {

private int size;

private int capacity;

public Stack(int capacity) {

this.capacity = capacity;

}

...

public void push(int element) {

if (size == capacity)

throw new Overflow();

size++;

}

...

public class Underflow extends RuntimeException {

}

public class Overflow extends RuntimeException {

}

}

"Sheer brilliance, old sport, old coot, old sod! But enough of these mundane machinations. It's time to make this sad excuse of a program begin to act like a stack. So when you push an element, I must require that you pop that self-same element!"

@Test

public void shouldPopZeroWhenZeroIsPushed() {

stack.push(0);

assertThat(stack.pop(), equalTo(0));

}

"I'm afraid, dear Avery, that I can make that pass rather trivially." I cursed myself under my breath for succumbing to his banter.

public int pop() {

if (size == 0)

throw new Underflow();

--size;

return `**0**`;

}

"Devilishly clever my boy! You parried my thrust with just a flick of your wrist! But can you flick this away just as casually?"

@Test

public void shouldPopOneWhenOneIsPushed() {

stack.push(1);

assertThat(stack.pop(), equalTo(1));

}

I forced myself to avoid the banter. "This one is going to require a variable. And once again, notice that we are replacing something specific, with something more general."

public class Stack {

...

private int element;

...

public void push(int element) {

if (size == capacity)

throw new Overflow();

size++;

this.element = element;

}

public int pop() {

if (size == 0)

throw new Underflow();

--size;

return element;

}

...

}

"Oh, ho! Yes, again, the constant is replaced with a variable. Specifics become generics. Well done, Alphonse! Well done! But as yet this beast behaveth not as ought a stack. Therefore shall I maketh you to perform a true FIFO operation!"

Why did he add that old-english twist? Was I distracting him? Was he looking ahead to the end-game and losing concentration?

@Test

public void PushedElementsArePoppedInReverseOrder() {

stack.push(1);

stack.push(2);

assertThat(stack.pop(), equalTo(2));

assertThat(stack.pop(), equalTo(1));

}

"OK, Avery, now watch this carefully."

public class Stack {

...

private int elements`**[]**`;

public Stack(int capacity) {

this.capacity = capacity;

elements = new int[capacity];

}

public void push(int element) {

if (size == capacity)

throw new Overflow();

element`**s[**`size++`**]**` = element;

}

public int pop() {

if (size == 0)

throw new Underflow();

return element`**s[**`--size`**]**`;

}

...

}

"Do you see what happened Avery? We transformed that element variable into something more general than a variable. We transformed it into an array."

Avery just looked at the code with his brows knitted together. His normally bulging eyes bulged even further. I could see the wheels turning in his head.

"Alphonse, do you realize that you didn't delete that size code? You just moved it around."

"What do you mean?"

"I mean that all that silly code that we wrote at first, the code for the size variable in order to pass the initial tests. I usually think of that as throwaway code - just something to do to get the early tests to pass. But we didn't delete that code; we *moved* it. We moved the size++ and --size into array subscripts."

"Yeah." I said. "Maybe that code wasn't so silly after all. It certainly wasn't throwaway. But did you notice the *transformations*, Avery? We transformed specific code like constants, into generic code like variables and arrays."

"Yeah, I *did* notice that Alphonse, and that means that from one test to the next we were generalizing and moving code."

"We also *added* code like the constants, and the if statements for the exceptions, and the increments and decrements."

"Yeah! So the process of changing the production code from test to test is not one of rewriting so much as it is of adding, moving, and generalizing."

"That's cool!" I said. "It means that none of the code we write to pass the early tests is wasted code; it's just code that's incomplete, not properly placed, or not general enough. It's not that the code is wrong, it's just - what's the word?"

"Degenerate!" Avery said. "The early code is degenerate. It's not silly or wasted; it just young! It needs to evolve. That earlier code is the *progenitor* of the latter code."

"I wonder." I said.

"What?"

"I wonder if this is *always* true. Can you always evolve code, from test to test, by adding, moving, or generalizing it? Is the process of TDD really just a set of successive generalizations constrained by tests?"

"I don't know. Let's try the Prime Factors kata..."
