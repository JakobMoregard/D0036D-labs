package lab_2.model;

import java.io.*;
import java.util.*;
import javax.xml.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
*
* GetXML reads a xml file containing place names and geographical markers for real places and stores these in a ArrayList of places
* 
*  @author Jakob Moreg�rd
*  @version 1
*  @since 30/9/2019
*/

public class GetXML {
	
	private ArrayList<Place> places;
	
	/**
	 * The constructor reads a xml file and stores places in a ArrayList
	 */
	public GetXML() {
		places = new ArrayList<Place>();	//initierar en ArrayList f�r Place instanser
		
		try {
			//�ppnar och l�ser igenom places.xml
			File fXmlFile = new File("C:\\Eclipse workspace\\D0036D_Lab2\\places.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
					
			doc.getDocumentElement().normalize();	//formaterar texten i filen
					
			NodeList nList = doc.getElementsByTagName("locality");	//skapar en nodelist med element f�r namn
			NodeList nList2 = doc.getElementsByTagName("location");	//skapar en nodelist med element f�r geografisk placering

			for (int i = 0; i < nList.getLength(); i++) {	//loopar igen f�r antalet localitys som finns i xml filen

				Node nNode = nList.item(i);
				Node nNode2 = nList2.item(i);
						

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					Element eElement2 = (Element) nNode2;
					
					Place p = new Place();	//skapar en ny instans av Place
					
					p.name = eElement.getAttribute("name");		//l�gger till namnet i instansen
					p.altitude = eElement2.getAttribute("altitude");	//l�gger till altituden
					p.latitude = eElement2.getAttribute("latitude");	//l�gger till latituden
					p.longitude = eElement2.getAttribute("longitude");	//l�gger till longituden
					places.add(p);		//l�gger till instansen i ArrayListen
				
					
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return a ArrayList of places
	 */
	
	public ArrayList<Place> getPlaces() {
		return places;
	}
	
	/**
	 * @param index the index of place in ArrayList
	 * @return name in place
	 */
	
	public String getName(int index) {
		return places.get(index).name;
	}
}