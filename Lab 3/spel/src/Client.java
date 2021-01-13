package lab_3_testclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * The Client class handles the network communication of the program.
 *@author Jakob Moregård
 *@version 1
 *@since 20/10/2019
 */
public class Client {
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buffer;
    
    /**
     * The constructor creates a UDP datagramSocket
     */
    public Client() {
        try {
			socket = new DatagramSocket();
			address = InetAddress.getByName("::1");
		} 
        catch (SocketException e) {
			e.printStackTrace();
		} 
        catch (UnknownHostException e) {
			e.printStackTrace();
		}
        
    }
    
    /**
     * Method sends UDP packets to local-host containing coordinates for a Grid.
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param c The color token.
     */
    public void sendCommand(String x, String y, String c) {
    	
    	String moveMessage = x + ":" + y + ":" + c;
    	buffer = moveMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, 49000);

		try {
			socket.send(packet);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
    }
}