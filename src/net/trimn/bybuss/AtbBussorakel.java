package net.trimn.bybuss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class is used to communicate with AtB.
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
			uri = new URI(
					"http",
					"m.atb.no",
					"/xmlhttprequest.php?service=routeplannerOracle.getOracleAnswer&question=",
					null);
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
		String content = null;
		URLConnection conn = null;
		Scanner sc = null;

		String tmpUri = this.uri.toString().replace("%3F", "?")
				+ this.getQuestion().replace("%3F", "?");
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
			answer = (content.replace(". Buss", ".\n\nBuss").replace(".  Tidene", ".\n\nTidene"));
		} else {
			answer = "Søk kunne ikke gjennomføres. Sjekk om du er tilkoblet nettet (mobilnett/WiFi)";
		}

	}

	/**
	 * Checks if the last word in String is a date and month. This class fixes
	 * the Formating bug for some special cases.
	 * 
	 * @param s
	 * @return String
	 */
	private boolean checkEnding(String s) {
		ArrayList<String> month = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("Jan");
				add("Feb");
				add("Mar");
				add("Apr");
				add("May");
				add("Jun");
				add("Jul");
				add("Aug");
				add("Sep");
				add("Oct");
				add("Now");
				add("Des");
			}
		};
		boolean result = false;

		for (String s2 : month) {
			if ((s.substring(s.length() - 3)).equals(s2)) {
				result = true;
				break;
			} else {
				try {
					Integer.parseInt(s.substring(s.length() - 2).trim());
					result = true;
				} catch (NumberFormatException e) {
					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * Format answer from server.
	 * 
	 * @return String
	 */
	public String getAnswer() {
		return this.answer;
	}

}
