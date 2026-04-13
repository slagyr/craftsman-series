# The Craftsman: 55 Clean Code: C4 Poorly Written Comment

Robert C. Martin
24 February 2009

*Mar 10, 1945, 12:00:00 GMT*

*The worst of the Anthrax attack was over. More than twenty thousand people were dead, and many more lay sick and dying, but the quarantine orders put in place by president Wallace at the recommendation of Jonas Salk, had stopped the spread of the disease. The government was literally decimated. Congress had lost one in eight. The White House had lost one in ten. Worse yet, the Nimbus project had lost a full third of its scientists and workers. The military was still trying to count its dead.*

*"Hitler's agents have been in the country for years." J. Edgar. Hoover said to Wallace. "This attack was very carefully planned and executed. Infectious agents were released over a period of several days in locations known to be frequented by government personnel. Mr. President, we cannot let this go unanswered."*

*"We won't John." Wallace turned to Patton and DoLittle. "Gentlemen, what do you recommend?"*

*The two generals looked at each other and Patton, speaking around his cigar, responded. "Sir, an single Orion vessel could fly over Europe and Asia and drop thousands of atomic weapons on them day after day. After a week there'd be nothing left of the whole stinking continent. No city larger than 3,000 would be left standing." He took the cigar out of his mouth and gazed at the glowing coal. "The second vessel is nearly ready," He looked up with a grin. "We rather like the name:*Raptor*. Three thousand of the kiloton bombs we use for propellant will be weapon-ized and ruggedized to survive the trip back into the air. Give us 6 weeks and we'll be ready." Patton once again clenched the cigar between his teeth and stared defiantly at his commander-in-chief.*

*Dolittle squirmed in his chair. "But sir," he said. "What we don't understand is why they didn't mount a land invasion during all the confusion here. They could have*walked*right in here. I mean by all rights,*they*should be sitting here now instead of us."*

*Monday, 12 Mar 2002, 09:00*

"Hey, Avery, can I borrow you for a minute?"

Avery glared at me for a second, as though the weight of the ship were on his shoulders. Then he slowly got up and sat down next to me.

"What is it now?" he asked. Sometimes Avery's moods are tough to handle.

"Look at this code for a second, will you?" And I pointed at the screen.

/**

* Creates a relative url by stripping the common parts of the the url.

* @param url the to be stripped url

* @param baseURL the base url, to which the <code>url</code> is relative

* to.

* @return the relative url, or the url unchanged, if there is no relation

* beween both URLs.

*/

public static String createRelativeURL(final URL url, final URL baseURL) {

boolean sameProtocol = url.getProtocol().equals(baseURL.getProtocol());

boolean sameHost = url.getHost().equals(baseURL.getHost());

boolean samePort = url.getPort() == baseURL.getPort();

if (sameProtocol && sameHost && samePort) {

// If the URL contains a query, ignore that URL; do not

// attemp to modify it...

List urlName = parseName(getPath(url));

List baseName = parseName(getPath(baseURL));

String query = getQuery(url);

if (!isPath(baseURL)) {

baseName.remove(baseName.size() - 1);

}

// if both urls are identical, then return the plain file name...

if (url.equals(baseURL)) {

return (String) urlName.get(urlName.size() - 1);

}

int commonIndex = startsWithUntil(urlName, baseName);

if (commonIndex == 0) {

return url.toExternalForm();

}

if (commonIndex == urlName.size()) {

// correct the base index if there is some weird mapping

// detected,

// fi. the file url is fully included in the base url:

//

// base: /file/test/funnybase

// file: /file/test

//

// this could be a valid configuration whereever virtual

// mappings are allowed.

commonIndex -= 1;

}

final ArrayList retval = new ArrayList();

if (baseName.size() >= urlName.size()) {

final int levels = baseName.size() - commonIndex;

for (int i = 0; i < levels; i++) {

retval.add("..");

}

}

retval.addAll(urlName.subList(commonIndex, urlName.size()));

return formatName(retval, query);

}

return url.toExternalForm();

}

"Who wrote this smut?" Avery proclaimed after looking through it for a few seconds.

"I don't know." I said. "I found it buried in this old application I'm trying to learn."

Avery grimaced. "Where are the tests?"

"Yeah, about that..." I grinned knowingly.

Avery rolled his eyes and took on a disgusted demeanor. "OK, so there aren't any tests."

"None that came with the code, but I've written a couple so far."

"Good, let's see them." Avery commanded. So I waved a new screen into existence.

public class UrlToolTest {

@Test

public void canCreateSimpleRelativeUrl() throws Exception {

URL base = new URL("http://www.alphonse.net/alpha/");

URL full = new URL("http://www.alphonse.net/alpha/beta");

String relativeUrl = UrlTool.createRelativeURL(full, base);

assertEquals("beta", relativeUrl);

}

@Test

public void canHandleBackwardsReference() throws Exception {

URL base = new URL("http://www.alphonse.net/alpha/beta/");

URL full = new URL("http://www.alphonse.net/alpha");

String relativeUrl = UrlTool.createRelativeURL(full, base);

assertEquals("../../alpha", relativeUrl);

}

}

Avery looked a these for a few more seconds and then proclaimed: "Oh, I see, given two URLs it just figures out how to make one relative to the other."

"I think that's right", I said.

"OK, so why'd you call me over here?"

I pointed to the multi-line comment in the middle of the module and said: "I don't understand that comment."

"Who reads comments?" Avery said with a sneer. Then he looked closer. "Yeah, it's not really a literary masterpiece is it? ... I mean really." And then he glared at the screen for a second and pointed to the `fi` In the midst of the comment. "Fie? Fie? What the hell does Fie mean? Fee, Fie, Fo, Fum? Fie?" And he looked at me with this big silly incredulous grin on his face.

"Fie?" I asked.

"Fie!" Avery declared with enthusiasm.

"Could it mean: 'for instance'?" I asked.

"No!, it means Fie! FIE! Damn you!"

I could see this was degrading rapidly. "OK, I agree. Fie! But what the heck is he talking about in the rest of the comment"?

"Who the hell knows." Avery said, and then he looked closer. "I suppose that the "base index" must be the `commonIndex` variable."

"Maybe, though you'd think he'd have named the variable `baseIndex` in that case?"

"Maybe he did once, but then he changed the name of the variable. Who knows?"

"Do you know what he means by 'virtual mapping'?"

"God no. Is that a mapping that has virtue? Or a mapping that doesn't exist? Or a mapping that's just weird, like he says a few lines above? Who knows? I'm not even sure I know what he means by the term 'mapping', let alone 'virtual mapping'."

"And then what's all this stuff about 'configurations'? Configurations of what?"

Avery paused for a second and then shook his head. "Hell, Alphonse, why are you trying to understand this? The author couldn't even take the time to spell a word like 'wherever' correctly. So he clearly isn't interested in communicating. He was probably guilty about the mess he'd made in this function, so we wrote a perfunctory comment and then just walked away."

Then Avery jabbed his finger in the air and said: "Alphonse, I have the solution. I know what to do."

"You do?"

"Yes, I do." And he waved at the screen and deleted the comment. I had my sound effects set to make a popping noise for deletes. It was a very satisfying sound in this case.

"There." Said Avery. "Problem solved." And he went back and sat down at his workstation.

I pondered his solution for a moment, and realized he was probably right. That comment told me nothing useful at all. It may have meant something to the author at one time, but the author was clearly not trying to communicate with *me*.

I waved open the *Clean Code* heuristics and saw:

***C4: Poorly Written Comment***

*A comment worth writing is worth writing well. If you are going to write a comment, take the time to make sure it is the best comment you can write. Choose your words carefully. Use correct grammar and punctuation. Don't ramble. Don't state the obvious. Be brief.*
