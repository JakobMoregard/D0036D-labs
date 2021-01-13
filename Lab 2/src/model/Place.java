package lab_2.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Place creates a object representing a real location with variables for name, latitude, altitude, longitude, timestamp and a Document instance
 * @author Jakob Moreg�rd
 *@version 1
 *@since 30/9/2019
 */


public class Place {
	
	public String name, altitude, latitude, longitude;
	public long timeStamp;
	private Document data;
	
	/**
	 * The constructor initalizes the timestamp to 0.
	 */
	
	public Place() {
		timeStamp = 0;		
	}
	
	/**
	 * @return the name variable in a place
	 */
	public String toString() {
		return name;
	}
	
	/**
	 * @param document sets the document instance
	 * @param timeStamp sets the timeStamp variable
	 */
	public void setData(Document document, long timeStamp) {
		this.data = document;
		this.timeStamp = timeStamp;		
	}
	
	/**
	 * Reads temperature information from a document instance.
	 * @param time the time the user wants to check temperature for.
	 * @return the temperature of the specified time
	 */
	public float getTemp(String time) {
		NodeList nList = data.getElementsByTagName("time");		//skapar en nodelist med 'time' element
		String timeFormated = dateFormated(time);
		float temp = 237;

		for (int i = 0; i < nList.getLength(); i++) {

			Node nNode = nList.item(i);
			Node nNode2 = null;

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				Element eElement = (Element) nNode;
				String fromValue = eElement.getAttribute("from");
				String toValue = eElement.getAttribute("to");
				
				if (fromValue.equals(timeFormated) && toValue.equals(timeFormated)) {
					nNode2 = eElement.getFirstChild().getNextSibling().getFirstChild().getNextSibling(); //g�r ner tv� element djupt i dokumentet
					Element eElement2 = (Element) nNode2;
					String tempValue = eElement2.getAttribute("value");
					temp = Float.parseFloat(tempValue);	//sparar value som �r temperaturen
				}
			}
		}
		return temp;
	}
	
	private String dateFormated(String date) {
		
		ZoneId timeZoneID = ZoneId.of( "Europe/Oslo" );
		LocalTime localTime = LocalTime.now(timeZoneID);		//h�mtar lokal tid f�r tidszon
		LocalDate localDate = LocalDate.now();	//sparar datum

		String timeNow[] = localTime.toString().split(":");
		String timeWanted[] = date.split(":");
		int timeNowInt = Integer.parseInt(timeNow[0]);
		int timeWantedInt = Integer.parseInt(timeWanted[0]);
		
		//formaterar datum/tid
		if((timeWantedInt - timeNowInt) < 0) {
			return (localDate.plusDays(1) + "T" + date + ":00Z");	
		}
		else {
			return (localDate + "T" + date + ":00Z");
		}
	}
	

}
