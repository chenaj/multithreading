/* CIS427 Program 2
 * multi-threaded server 
 * Angela Chen
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class MultiThreadServer {

    public static final int SERVER_PORT = 2039;

    public static void main(String args[]) 
    {
	ServerSocket myServerice = null;
	Socket serviceSocket = null;

	// Try to open a server socket 
	try {
	    myServerice = new ServerSocket(SERVER_PORT);
	}
	catch (IOException e) {
	    System.out.println(e);
	}   
	System.out.println("Server is up, and running....");
	// Create a socket object from the ServerSocket to listen and accept connections.
	while (true)
	{
	    try 
	    {
		// Received a connection
		serviceSocket = myServerice.accept();
		System.out.println("MultiThreadServer: new connection from " + serviceSocket.getInetAddress());

		// Create and start the client handler thread
		ChildThread cThread = new ChildThread(serviceSocket);
		cThread.start();
	    }   
	    catch (IOException e) 
	    {
		System.out.println(e);
	    }
	}
    }
}
