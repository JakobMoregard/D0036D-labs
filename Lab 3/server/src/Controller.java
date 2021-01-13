package lab_3_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * The Controller class handles the network communications of the program.
 * 
 * @author Jakob Moregård
 * @version 1
 * @since 20/10/2019
 */
public class Controller extends Thread {
	
	protected DatagramSocket socket;
    private boolean runFlag;
    private byte[] buffer = new byte[65507];
    private Grid grid;
    
	/**
	 * The constructor creates a datagramSocket.
	 * @param grid The game Grid.
	 */
	public Controller(Grid grid) {
		this.grid = grid;
		
		try {
			socket = new DatagramSocket(49000, InetAddress.getByName("::1"));
		}
		catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method receives UDP packets and decodes the contents.
	 */
	public void run() {
        runFlag = true;
        
        while (runFlag) {
            DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);
            try {
            	System.out.println("Listening...");
				socket.receive(packet);
				System.out.println("Received packet");
				
	            
	            String moveCommand = new String(packet.getData(), 0, packet.getLength());
	            System.out.println(moveCommand);
	            
	            moveCommandDecode(moveCommand);
	            
			
            }
            catch (IOException e) {
				e.printStackTrace();
			}
        }
        socket.close();
    }

	private void moveCommandDecode(String moveCommand) {
		try {
			String[] moveCoordinates = moveCommand.split(":");
			int x = Integer.parseInt(moveCoordinates[0]);
			int y = Integer.parseInt(moveCoordinates[1]);
			int c = Integer.parseInt(moveCoordinates[2]);
			if((x > 200 || y > 200) || (c > 8)) {
				return;
			}
			grid.changePosition(x, y, c);
		}
        catch (NumberFormatException e) {
			return;
		}	
	}	
}