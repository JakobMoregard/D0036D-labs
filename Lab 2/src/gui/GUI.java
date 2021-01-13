package lab_2.gui;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

import lab_2.controller.*;
import lab_2.model.*;
import lab_2.Main;

/**
* GUI class creates a simple weather client for reading temperatures during different times of day of some predetermined locations.
* It needs a controller to handle the user inputs and to display information outputs to the user.
* 
* @author Jakob Moreg�rd
* @version 1
* @since 30/9/2019
*/


public class GUI implements Observer{
	
	private Controller control;
	private JButton updateButton, startButton;
	private JComboBox placeDropDown, timeDropDown;
	private JLabel placeLabel, timeLabel, durationLabel, actionLabel;
	private JTextField textField;
	
	/**
	 * The constructor creates the GUI with JButtons, JComboboxes, JLabels, a JTextField and determines the layout
	 * @param control instance of a Controller
	 */
	
	public GUI(Controller control) {
		
		this.control = control;
		control.addObserver(this);
		
		
		placeDropDown = new JComboBox();
		timeDropDown = new JComboBox();
		startButton = new JButton("Start");
		updateButton = new JButton("Update");
		placeLabel = new JLabel("Location");
		timeLabel = new JLabel("Time");
		durationLabel = new JLabel("Data cache time");
		actionLabel = new JLabel("");
		textField = new JTextField("10", 10);
		
		
		SpringLayout layout = new SpringLayout();
		
		JFrame frame = new JFrame("Weather");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocation(150,150);
	    
	    Container container = frame.getContentPane();
        container.setLayout(layout);
        
        Dimension d = new Dimension(600, 500);	//storleken p� GUI f�nstret
        container.setPreferredSize(d);
        container.setBackground(Color.WHITE);
		
		//skapar layouten p� GUIn
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, placeLabel, -150, SpringLayout.HORIZONTAL_CENTER, container);
		layout.putConstraint(SpringLayout.NORTH, placeLabel, 5, SpringLayout.NORTH, container);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, placeDropDown, 0, SpringLayout.HORIZONTAL_CENTER, placeLabel);
		layout.putConstraint(SpringLayout.NORTH, placeDropDown, 5, SpringLayout.SOUTH, placeLabel);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, timeLabel, 0, SpringLayout.HORIZONTAL_CENTER, container);
		layout.putConstraint(SpringLayout.NORTH, timeLabel, 5, SpringLayout.NORTH, container);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, timeDropDown, 0, SpringLayout.HORIZONTAL_CENTER, timeLabel);
		layout.putConstraint(SpringLayout.NORTH, timeDropDown, 5, SpringLayout.SOUTH, timeLabel);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, durationLabel, 150, SpringLayout.HORIZONTAL_CENTER, container);
		layout.putConstraint(SpringLayout.NORTH, durationLabel, 5, SpringLayout.NORTH, container);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, textField, 0, SpringLayout.HORIZONTAL_CENTER, durationLabel);
		layout.putConstraint(SpringLayout.NORTH, textField, 5, SpringLayout.SOUTH, durationLabel);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, updateButton, -50, SpringLayout.HORIZONTAL_CENTER, container);
		layout.putConstraint(SpringLayout.NORTH, updateButton, 250, SpringLayout.NORTH, container);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, startButton, 50, SpringLayout.HORIZONTAL_CENTER, container);
		layout.putConstraint(SpringLayout.NORTH, startButton, 250, SpringLayout.NORTH, container);

		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, actionLabel, 0, SpringLayout.HORIZONTAL_CENTER, container);
		layout.putConstraint(SpringLayout.SOUTH, actionLabel, -50, SpringLayout.SOUTH, container);
		
		
		//l�gger till elementen till GUIn
		container.add(placeDropDown);
		container.add(timeDropDown);
		container.add(updateButton);
		container.add(startButton);
		container.add(placeLabel);
		container.add(timeLabel);
		container.add(durationLabel);
		container.add(actionLabel);
		container.add(textField);
		
        frame.setContentPane(container);
        frame.pack();
        frame.setVisible(true);
        
 
        startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				control.getWeather((Place)placeDropDown.getSelectedItem(), (String)timeDropDown.getSelectedItem());
				//h�mtar in vilket st�lle och vilken tid som anv�ndaren vill ha data f�r
			}
		});
        
        updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				control.setCacheTime(textField.getText());	//updaterar hur l�nge anv�ndaren vill beh�lla datan fr�n textrutan
			}
		});
        
        listCreator(control.getList());
        frame.setContentPane(container);
        frame.pack();
        frame.setVisible(true);
	    
	}
	
	private void listCreator(ArrayList<Place> placeList) {
		for (Place place : placeList) {
			placeDropDown.addItem(place);
		}
		for (int i = 0; i <= 23; i++) {		//uppr�knare f�r att skapa dropdown meny f�r tider
			if (i > 9) {	//�r tiden efter 09:00
				timeDropDown.addItem(i + ":00");
			} else {		//�r tiden innan 09:00
				timeDropDown.addItem("0" + i + ":00");
			}

		}
	}
	     
	/**
	 * updates the messageLabel in the GUI
	 */
	public void update(Observable arg0, Object arg1) {
		actionLabel.setText(control.messageLabel);	//uppdaterar v�derdatan p� GUIn

	}
	     
}
