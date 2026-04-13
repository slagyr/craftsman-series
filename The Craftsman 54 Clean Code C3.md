# The Craftsman: 54 Clean Code: C3 Redundant Comment

Robert C. Martin
16 February 2009

*Feb 15, 1945, 08:00:00 GMT*

*Linus Pauling was on a conference call with Washington, waiting for the status meeting to begin. He had very good news about the latest experiments for shaping the nuclear charges that propel the Orion crafts. The next generation vehicle could be 20% more efficient as a result.*

*"Linus, are you there" Said Bill Davies, coordinator of the meeting.*

*"I'm (cough, cough) here." He said. "Excuse me, I can't seem to shake this awful cold." He said.*

*"I know what you mean! We've got a dozen or so people out sick today."*

*Jamie Carlson, also on the call, said: "Yeah, there's a bug going around at Brookhaven too. Don't you just love winter?"*

*"Good Morning Folks." Vice President Truman said, as he joined the call. "Shall we get...(cough)...excuse me... started?"*

*Monday, 11 Mar 2002, 15:00*

I was staring at the code on my screen. It was about three years old, and was pretty well written. The thing I didn't like about it was that it was littered with redundant comments. I looked up and spotted Jerry pairing with Avery in the next cube.

"Jerry, could you come over here and look at this?"

"Jerry looked up and said, "I've only got another 10 minutes in the current tomato. Can it wait till then?"

"Tomato?" But Jerry just started back expectantly. "Uh, yeah, sure. Whenever you're ready."

"Thanks." And Jerry turned back to Avery and whatever it was they were working on.

So I scrolled through the code again:

/**

* QuickEntryMediator. This class takes a JTextField and a JList.

* It assumes that the user will type characters into the JTextField

* that are prefixes of entries in the JList. It automatically selects

* the first item in the JList that matches the current prefix in the

* JTextField.

* <p/>

* If the JTextField is null, or the prefix does not match any element

* in the JList, then the JList selection is cleared.

* <p/>

* There are no methods to call for this object. You simply create it,

* and forget it. (But don't let it be garbage collected...)

* <p/>

* Example:

* <p/>

* JTextField t = new JTextField();

* JList l = new JList();

* <p/>

* QuickEntryMediator qem = new QuickEntryMediator(t, l);

* // that's all folks.

* @param t The JTextField from which abbreviations will be gotten.

* @param l The JList that will be automatically scrolled to match.

* @author Jerry, Jeremy.

* @date 30 Jun, 1999 2113 (DYSON)

*/

public class QuickEntryMediator {

private JTextField itsTextField;

private JList itsList;

public QuickEntryMediator(JTextField t, JList l) {

itsTextField = t;

itsList = l;

itsTextField.getDocument().addDocumentListener

(

new DocumentListener() {

public void changedUpdate(DocumentEvent e) {

textFieldChanged();

}

public void insertUpdate(DocumentEvent e) {

textFieldChanged();

}

public void removeUpdate(DocumentEvent e) {

textFieldChanged();

}

} // new DocumentListener

); // addDocumentListener

} // QuickEntryMediator()

private void textFieldChanged() {

String prefix = itsTextField.getText();

if (prefix.length() == 0) {

itsList.clearSelection();

return;

}

ListModel m = itsList.getModel();

boolean found = false;

for (int i = 0; found == false && i < m.getSize(); i++) {

Object o = m.getElementAt(i);

String s = o.toString();

if (s.startsWith(prefix)) {

itsList.setSelectedValue(o, true);

found = true;

}

}

if (!found) {

itsList.clearSelection();

}

} // textFieldChanged

} // class QuickEntryMediator

A few minutes later I heard a small bell ringing. I'd heard it several times that day, but had ignored it. This time, however, it coincided perfectly with Jerry getting up and coming over to his workstation.

"I thought you were supposed to be working with Adelaide." Jerry said.

"Oh, yeah I was, but Jean stepped in."

Jerry rolled his eyes and nodded wryly. "Yeah, that happens."

"What's up with the bell?" I asked.

"Oh that? I'll tell you later. What did you need me for?"

I heaved a sigh. "Did you write this code?"

Jerry looked it over for about half a minute and then said: "Oh, yeah, this is part of the *Ice* system that Jeremy and I wrote a few years back. Whoo, what a hoot that project was!"

I couldn't imagine anything being a hoot where Jeremy was concerned, but I let that go. "I thought you told me that comments shouldn't be redundant."

"Right!" Jerry said, "That's one of Mr. C's rules."

"Yeah, I know." I said. "Its C3: '*A comment is redundant if it describes something that adequately describes itself.*'

"Yep, that's the one."

"So how come you've got all this redundancy in the javadocs in this module?"

"What redundancy is that?"

"Well, look." I said. "The `@param` statements, and the initial opening paragraph that describers the constructor arguments, and the comments on the closing braces, and..."

"Oh, yeah, that was back when Javadoc was brand new. We were so excited about all that automatically generated documentation that we kinda went overboard with it. But Mr. C. got wind of it (I think Jean told him) and he put a stop to it."

I nodded. It was hard for me to internalize that things were so different just a couple of years ago. "I guess things change a lot around here."

"You bet they do." Jerry agreed. "We keep on learning new stuff all the time. I think that's kind of what it means to *be* a software developer. God help us if we ever stop learning."

I nodded. "So I shouldn't use this code as a model for my own code, should I?"

"Well, at least not the comments." Jerry smiled. "In fact, why don't you refactor the comments in that module so that they conform to our current style."

"Uh, er, sure." I hadn't asked him over here to give me a new task.

"Was that all you needed?" He asked, clearly anxious to get back to working with Avery.

"Yeah, thanks. I'll see you later at dinner?"

"Uh, No, not tonight Alphonse, I've got a date with Jazzy, er, Jasmine."

"OK, well later then."

Jerry nodded with a smile and then turned back to the cubicle with Avery. I heard him wind something up, and notice a ticking sound. I started typing.

/**

* Selects the line in the list matched by the abbreviation in the text

* field. If no line matches then the selection is cleared.

*/
