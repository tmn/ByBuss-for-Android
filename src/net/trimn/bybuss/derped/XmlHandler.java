package net.trimn.bybuss.derped;

import net.trimn.bybuss.Holdeplass;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlHandler extends DefaultHandler {
	
	public static Holdeplass holdeplass = null;
	
	public static Holdeplass getHoldeplass() {
		return holdeplass;
	}
	
	public static void setHoldeplassList(Holdeplass holdeplass) {
		XmlHandler.holdeplass = holdeplass;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if (localName.equals("osm")) {
			holdeplass = new Holdeplass();
		} else if (localName.equals("node")) {
			
		}
	}
	
}
