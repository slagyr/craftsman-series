var agt = navigator.userAgent.toLowerCase();

// version number
var browserVersion_major = parseInt(navigator.appVersion);
var browserVersion_minor = parseFloat(navigator.appVersion);

var browser_ie    = ((agt.indexOf("msie") != -1) && (agt.indexOf("opera") == -1));
var browser_ie3   = (browser_ie && (browserVersion_major < 4));
var browser_ie4   = (browser_ie && (browserVersion_major == 4) && (agt.indexOf("msie 4")!= -1) );
var browser_ie5up = (browser_ie && !browser_ie3 && !browser_ie4);

var browser_nav    = ((agt.indexOf('mozilla')!= -1) && (agt.indexOf('spoofer') == -1)
				 && (agt.indexOf('compatible') == -1) && (agt.indexOf('opera') == -1)
				 && (agt.indexOf('webtv')== -1) && (agt.indexOf('hotjava') == -1));
var browser_nav6up = (browser_nav && (browserVersion_major >= 5));