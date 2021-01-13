package lab_2.model;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * API handles the network communication with a weather server and stores weather data in a document instance.
 * @author Jakob Moreg�rd
 *@version 1
 *@since 30/9/2019
 */
public class API {
	
	/**
	 * Sends url requests containing geographic markers to a server and saves the respond information in a document instance.
	 * @param place the specific place the weather data is wanted for.
	 * @return the document instance containing the weather data.
	 */
	public Document Get(Place place) {
		URL connectionURL;
		Document document = null;	//skapar Document instans d�r temperatur data lagras
		String url = "https://api.met.no/weatherapi/locationforecast/1.9/?lat=" + place.latitude + "&lon=" + place.longitude + "&msl=" + place.altitude;
		//str�ng som ger en geografisk plats till servern som URL
		
		try {
			connectionURL = new URL(url);
			HttpURLConnection connectionHTTP = (HttpURLConnection) connectionURL.openConnection();	//startar n�tverkskommunikationen
			connectionHTTP.setRequestMethod("GET");	//skickar GET
			connectionHTTP.addRequestProperty("Accept", "application/xml");

			DocumentBuilderFactory docBuildFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuild = docBuildFactory.newDocumentBuilder();		

			String responseMessage = Integer.toString(connectionHTTP.getResponseCode());
			
			System.out.println("");
			System.out.println("GET request sent to URL : " + url);	
			System.out.println("Server response message: " + responseMessage);	
			
			document = docBuild.parse(connectionHTTP.getInputStream());		//h�mtar in v�derdatan som f�s av servern och l�gger in i Document instansen
			document.getDocumentElement().normalize();		
			
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
		//diverse felhantering
		
		return document;		//returnerar Document inneh�llande temperatur

	}
}


