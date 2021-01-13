package lab_3_testclient;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * The Client_GUI class is both the Main class of the client as well 
 * @author Jakob Moregård
 * @version 1
 * @since 20/10/2019
 */
public class Client_GUI {
	
	private JFrame frame;
	private JButton moveButton;
	
	private JLabel yLabel;
	private JLabel xLabel;
	private JLabel colorLabel;

	private JTextField yTextField;
	private JTextField xTextField;
	private JTextField colorTextField;

	/**
	 * The constructor creates the layout and look of the client
	 * @param client the Client.
	 */
	Client_GUI(Client client){
		
		frame = new JFrame("Client"); 
		
		//Submit move button
		moveButton = new JButton("Move");    
		moveButton.setBounds(60, 190, 180, 50);
		moveButton.setBackground(Color.DARK_GRAY);
		moveButton.setForeground(Color.LIGHT_GRAY);
		
		//X-coordinate label
		xLabel = new JLabel();
		xLabel.setText("Enter X Coordinate:");
		xLabel.setForeground(Color.LIGHT_GRAY);
		xLabel.setBounds(50, 30, 150, 30);
		
		//Y-coordinate label
		yLabel = new JLabel();		
		yLabel.setText("Enter Y Coordinate:");
		yLabel.setForeground(Color.LIGHT_GRAY);
		yLabel.setBounds(50, 80, 150, 30);
		
		//Color label
		colorLabel = new JLabel();
		colorLabel.setText("Enter Color Token:");
		colorLabel.setForeground(Color.LIGHT_GRAY);
		colorLabel.setBounds(50, 130, 150, 30);
		
		//X-coordinate textfield
		xTextField = new JTextField();
		xTextField.setBounds(200, 30, 50, 30);
		xTextField.setBackground(Color.DARK_GRAY);
		xTextField.setForeground(Color.LIGHT_GRAY);
		
		//Y-coordinate textfield
		yTextField = new JTextField();
		yTextField.setBounds(200, 80, 50, 30);
		yTextField.setBackground(Color.DARK_GRAY);
		yTextField.setForeground(Color.LIGHT_GRAY);
		
		//Color token textfield
		colorTextField = new JTextField();
		colorTextField.setBounds(200, 130, 50, 30);
		colorTextField.setBackground(Color.DARK_GRAY);
		colorTextField.setForeground(Color.LIGHT_GRAY);
		
		//adds components to frame
		frame.add(yLabel);
		frame.add(xLabel);
		frame.add(colorLabel);
		frame.add(yTextField);
		frame.add(xTextField);
		frame.add(colorTextField);
		frame.add(moveButton);    
		frame.setSize(300,300); 
		frame.setLocation(1000, 100);;
		frame.setLayout(null);    
		frame.setVisible(true); 
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Button listener
		moveButton.addActionListener(new ActionListener() {
	        
			@Override
			public void actionPerformed(ActionEvent arg0) {
					client.sendCommand(xTextField.getText(), yTextField.getText(), colorTextField.getText());
			}          
		});
	}         
	
	/**
	 * The main function creates a new Client and new Client_GUI.
	 * @param args startup argument.
	 */
	public static void main(String[] args) {
		Client client = new Client();
		new Client_GUI(client);    
	}    
}
