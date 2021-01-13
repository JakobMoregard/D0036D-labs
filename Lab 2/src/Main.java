package lab_2;

import lab_2.gui.*;
import lab_2.model.*;
import lab_2.controller.*;

/**
* Main class creates a GUI instance with a new Controller which starts the weather client
* 
* @author Jakob Moreg�rd
* @version 1
* @since 30/9/2019
*/

public class Main {
	
	/**
	 * Starts the GUI
	 * @param args input argument
	 */
	public static void main(String[] args) {
		GUI start = new GUI(new Controller());	//startar GUIn
	}
}
