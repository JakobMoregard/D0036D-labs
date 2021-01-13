package lab_3_server;


/**
 * The Main class for this program, it creates a new Grid and new GamePanel.
 * 
 * @author Jakob Moregård
 * @version 1
 * @since 20/10/2019
 */
public class Main {
	
	/**
	 * Starts the GUI
	 * @param args input argument
	 */
	public static void main(String[] args) {
		Grid grid = new Grid();
		GamePanel game = new GamePanel(grid);

	}
}
