package lab_3_server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The GamePanel class is responsible for the graphical look of the Grid and it paints the tokens
 * 
 * @author Jakob Moregård
 * @version 1
 * @since 20/10/2019
 */
public class GamePanel extends JPanel implements Observer{

	private final int UNIT_SIZE = 4;
	private Grid grid;
	private Controller controller;
	private JPanel panel;
	private JFrame frame;
	private Dimension dimension;
	
	
	/**
	 * The constructor sets up the layout and colors of the Grid
	 * @param grid The Grid it draws.
	 */
	public GamePanel(Grid grid) {
		
		this.grid = grid;
		controller = new Controller(grid);
		grid.addObserver(this);
		
		dimension = new Dimension(grid.getSize() * UNIT_SIZE + 1, grid.getSize() * UNIT_SIZE + 1);
		this.setMinimumSize(dimension);
		this.setPreferredSize(dimension);
		this.setBackground(Color.DARK_GRAY);
		
		frame = new JFrame("Grid");
		frame.setLocation(0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		
		panel.add(this);

		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
		
		controller.run();
	}
	
	/**
	 * Method calls private methods to paint the grid and the tokens.
	 * @param g Graphics g
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintGrid(g, grid);
		paintPosition(g, grid);
	}
	
	
	private void paintGrid(Graphics g, Grid grid) {
		for (int i = 0; i <= grid.getSize(); i++) {
			for (int n = 0; n <= grid.getSize(); n++) {
				g.setColor(Color.GRAY);
				g.drawRect(i * UNIT_SIZE, n * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
			}
		}
	}
	
	private void paintPosition(Graphics g, Grid grid){
        int row;
        int colum;

        for(int i = 0; i < grid.getSize(); i++){
            for(int j = 0; j < grid.getSize(); j++){
                row = i * UNIT_SIZE;
                colum = j * UNIT_SIZE;
                switch(grid.getPosition(i, j)) {
                	
                	case 0:
                		break;
                
                	case 1:
                		g.setColor(Color.RED);
                        g.fillRect(colum, row, UNIT_SIZE, UNIT_SIZE);
                        
                        break;
                        
                	case 2:
                		g.setColor(Color.GREEN);
                		g.fillRect(colum, row, UNIT_SIZE, UNIT_SIZE);
                        break;
                        
                	case 3:
                		g.setColor(Color.BLUE);
                		g.fillRect(colum, row, UNIT_SIZE, UNIT_SIZE);
                        break;
                    
                	case 4:
                		g.setColor(Color.YELLOW);
                		g.fillRect(colum, row, UNIT_SIZE, UNIT_SIZE);
                        break;
                        
                	case 5:
                		g.setColor(Color.ORANGE);
                		g.fillRect(colum, row, UNIT_SIZE, UNIT_SIZE);
                        break;
                        
                	case 6:
                		g.setColor(Color.CYAN);
                		g.fillRect(colum, row, UNIT_SIZE, UNIT_SIZE);
                        break;
                    
                	case 7:
                		g.setColor(Color.MAGENTA);
                		g.fillRect(colum, row, UNIT_SIZE, UNIT_SIZE);
                        break;
                        
                	case 8:
                		g.setColor(Color.PINK);
                		g.fillRect(colum, row, UNIT_SIZE, UNIT_SIZE);
                        break;
                }
            }
        }
    }
	
	/**
	 * @return grid the Grid that is being drawn.
	 */
	public Grid getGrid() {
		return grid;
	}
	
	/**
	 * Method repaints the Grid when updated.
	 */
	public void update(Observable arg0, Object arg1) {
		this.repaint();
	}
}
