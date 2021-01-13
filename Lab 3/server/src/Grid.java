package lab_3_server;

import java.util.*;

/**
 * The Grid class creates a two dimensional grid using a ArrayList and IntegerArrays.
 * 
 * @author Jakob Moregård
 * @version 1
 * @since 20/10/2019
 */
public class Grid extends Observable{
	
	private final int SIZE = 201;
	private final int EMPTY = 0;
	ArrayList<int[]> xCoordinates;
	
	/**
	 * The constructor creates the Grid.
	 */
	public Grid() {
	
		xCoordinates = new ArrayList<int[]>(SIZE);
		int [] yCoordinates = new int[SIZE];
	
		for(int i = 0; i < SIZE; i++) {
			yCoordinates[i] = EMPTY;
		}
		
		for(int i = 0; i < SIZE; i++) {
			xCoordinates.add(i, yCoordinates.clone());
		
		}
	}
	
	/**
	 * @return the one dimensional size of the Grid.
	 */
	public int getSize() {
		return SIZE;
	}
	
	/**
	 * Method returns a requested location in the Grid.
	 * @param y The requested Y-coordinate.
	 * @param x The requested X-coordinate.
	 * @return the X and Y coordinates in the Grid.
	 */
	public int getPosition(int y, int x) {
		return xCoordinates.get(x)[y];
	}
	
	/**
	 * The method changes where color tokens are located in the Grid.
	 * @param x The requested X-coordinate
	 * @param y The requested Y-coordinate.
	 * @param color the token for requested color.
	 */
	public void changePosition(int x, int y, int color) {
		for(int i = 0; i < xCoordinates.size(); i++){
            for(int j = 0; j < xCoordinates.size(); j++){
            	if(xCoordinates.get(i)[j] == color) {
            		xCoordinates.get(i)[j] = 0;
            	}
            }
		}
		xCoordinates.get(x)[y] = color;
		setChanged();
		notifyObservers();
	}

}
