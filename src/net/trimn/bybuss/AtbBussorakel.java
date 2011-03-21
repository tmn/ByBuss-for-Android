package net.trimn.bybuss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * 
 * @author tmn
 */
public class AtbBussorakel {
	private String question;
	private String answer;
	private URI uri;
	
	
	/**
	 * Create an instance of Busstuc
	 */
	public AtbBussorakel() {
		try {
			uri = new URI("http", "m.atb.no", "/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=", null);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Set question
	 * 
	 * @param question
	 */
	public void setQuestion(String question) {
		this.question = question;
	}
	
	
	/**
	 * Return question 
	 * 
	 * @return String
	 */
	public String getQuestion() {
		return this.question;
	}
	

	
	
	/**
	 * Get answer fomr Bussorakelet
	 * 
	 * @return String
	 */
	public void ask() {
		String content 			= null;
		URLConnection conn 		= null;
		Scanner sc 				= null;
		
		String tmpUri = this.uri.toString().replace("%3F", "?") + this.getQuestion().replace("%3F", "?");
		tmpUri = tmpUri.replace(" ", "%20");
		
		try {
			conn = new URL(tmpUri).openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			sc = new Scanner(conn.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (sc != null) {
			sc.useDelimiter("\\Z");
			content = sc.next();
			answer = content;
		} else {
			answer = "Søk kunne ikke gjennomføres. Sjekk om du er tilkoblet nettet (mobilnett/WiFi)";
		}

	}



	/**
	 * Format answer from server.
	 * 
	 * @return String
	 */
	public String getAnswer() {
		String tmpAnswer = answer;
		tmpAnswer = tmpAnswer.replace(" kl. ", " kl ");
		tmpAnswer = tmpAnswer.replace("  ", " ");
		String[] answerFormated = tmpAnswer.split("\\. ");
		
		String finalAnswer = "";
		
		for (String s : answerFormated) {
			finalAnswer += s.trim() + ".\n\n";
		}
		return finalAnswer;
	}
	
}
