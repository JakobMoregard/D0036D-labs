package lab_2.controller;

	
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;

import lab_2.model.*;


/**
 * Controller is responsible for setting and keeping track of the weather data time-stamp as well as outputting temperature information.
 * @author Jakob Moreg�rd
 * @version 1
 * @since 30/9/2019
 */
public class Controller extends Observable {
		
	private GetXML xmlreader;
	private API weatherApi;
	public String messageLabel = "";
	private long cacheTime = 600;
		
	/**
	 * The constructor creates an new instance of GetXML and API
	 */
	public Controller() {
		xmlreader = new GetXML();	//skaffar inl�sning av places.xml
		weatherApi = new API();
	}
		
		
		/**
		 * @return a ArrayList containing places
		 */
		public ArrayList<Place> getList() {
			return xmlreader.getPlaces();
		}
		
		
		/**
		 * method checks if the time-stamp for the current place is to old or not, if to old it fetches new data,
		 * then it writes the information to the messageLabel.
		 * @param place that the user wants temperature of.
		 * @param time that the user wants temperature of.
		 */
		public void getWeather(Place place, String time) {
			
			if ((place.timeStamp + cacheTime) < (System.currentTimeMillis()/1000)) {  	//kollar om anv�ndaren vill ha ny v�derdata
				place.setData(weatherApi.Get(place), System.currentTimeMillis()/1000);  //h�mtar ny data och s�tter en ny timestamp p� det
			}
			
			messageLabel = ( place.getTemp(time) + "\u00B0" + "C" + " is the temparature in " + place + " at " + time); //skapar utskriften i GUIn
			setChanged();
			notifyObservers();
				
		}
			
		/**
		 * Changes the time to live for the temperature data from the default. 	
		 * @param time that the user wants to cache the data.
		 */
		public void setCacheTime(String time) {
			cacheTime = Integer.parseInt(time)*60;	//hur l�nge vill anv�ndaren spara v�derdatat
		}
		
}

