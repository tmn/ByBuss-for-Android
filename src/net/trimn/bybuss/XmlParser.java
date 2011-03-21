/**
 * 
 * Denne klassen leser og returnerer et ArrayList med alle holdeplassene fra XML-fila
 * 
 * Bussholdeplassene hentes fra OpenStreetMap
 * 
 * @author Tri M. Nguyen
 * 
 */

package net.trimn.bybuss;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class XmlParser {
	private String filename;
	private Document dom;
	private ArrayList<Holdeplass> list = new ArrayList<Holdeplass>();
	private DocumentBuilderFactory dbf;
	private InputStream xmlFile;



	public XmlParser() {
		parseXml();
		parseDocument();
	}


	public XmlParser(InputStream io) {
		xmlFile = io;
		parseXml();
		parseDocument();
	}

	public void parseXml() {
//		try {
//			FileInputStream xml = new FileInputStream(xmlFile);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		URL url;
//		URLConnection urlConn = null;
//
//		try {
//			url = new URL("http://xapi.openstreetmap.org/api/0.6/node%5Bhighway=bus_stop%5D%5Bbbox=10.2962,63.4022,10.539,63.453%5D");
//			urlConn = url.openConnection();
//		} catch (IOException ioe) {
//			ioe.printStackTrace();
//		}
		
		dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
//			dom = db.parse(urlConn.getInputStream());
			dom = db.parse(xmlFile);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	public String getTest() {
		return filename;
	}
	
	public void parseDocument() {
		// root element
		Element docEle = dom.getDocumentElement();
		
		// get nodelist of elements
		NodeList nl = docEle.getElementsByTagName("node");
		
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				// get the employee element
				Element el = (Element)nl.item(i);
				
				// get the Employee object
				Holdeplass h = getHoldeplass(el);
				
				// add it to list
				list.add(h);
			}
		}

	}
	
	private Holdeplass getHoldeplass(Element elem) {
		double lat = Double.parseDouble(elem.getAttribute("lat"));
		double lon = Double.parseDouble(elem.getAttribute("lon"));
		
		NodeList nodeList = elem.getElementsByTagName("tag");
		
		String name = null;
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (((Element)nodeList.item(i)).getAttribute("k").equals("name")) {
				name = ((Element)nodeList.item(i)).getAttribute("v");
			}
		}
		
		Holdeplass h = new Holdeplass(name, lat, lon);
		return h;
	}
	
	public ArrayList<Holdeplass> getHoldeplasser() {
		return list;
	}
	
//	private void printData() {
//		System.out.println("No of Nodes " + list.size());
//		
//		for (Holdeplass h : list) {
//			System.out.println(h.toString());
//		}
//	}
	
	
//	public static void main(String[] args) {
//		try {
//			
//			XmlParser parser = new XmlParser("/resources/buss.xml");
//		} catch (RuntimeException e) {
//			e.printStackTrace();
//		}
////		parser.printData();
//	}

	
	
	
	
	
//	class Node {
//		String name;
//		String id;
//		String age;
//		String type;
//		public Node() {
//			// TODO Auto-generated constructor stub
//		}
//
//		public Node(String name, int id, int age, String type) {
//			// TODO Auto-generated constructor stub
//		}
//
//		public Node(String name, String id, String age, String type) {
//			this.name = name;
//			this.id = id;
//			this.age = age;
//			this.type = type;
//		}
//		
//		@Override
//		public String toString() {
//			return name + " - " + id + " " + age + " " + type;
//		}
//	}
}
