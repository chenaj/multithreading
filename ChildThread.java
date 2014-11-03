/* CIS427 Program 2
 * multi-threaded server 
 * Angela Chen
 */


import java.io.*;
import java.net.Socket;
import java.util.Vector;
import java.util.ArrayList;

public class ChildThread extends Thread 
{
   static  Vector<ChildThread> handlers = new Vector<ChildThread>(20);
	private static String[][] users ={ {"root","john","david", "mary"},{"root01","john01","david01", "mary01"}};
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
	private PrintStream os;
	public String userID = new String("Unknown");			// if a user is not logged in, they are defaulted to "Unknown"
		
    public ChildThread(Socket socket) throws IOException 
    {
  		this.socket = socket;
  		in = new BufferedReader(
  			new InputStreamReader(socket.getInputStream()));
  		out = new PrintWriter(
  			new OutputStreamWriter(socket.getOutputStream()));
  		os = new PrintStream(socket.getOutputStream());
    }

    public void run() 
    {
  		String line;				// Taken in as user's input
  		boolean isLoggedIn=false;
  		boolean isRoot=false;
  		boolean activeUser=false;
  		FileInputStream fstream;	//file stream for the list of message of the day
  		String messageOfDay;
  		String pMessage;
  		ArrayList<String> messageList;
  		messageList = new ArrayList<String>();
  		int messCount = 0;
  		String delim = "\n";
		
		
		synchronized(handlers) 
		{
			// add the new client in Vector class
			handlers.addElement(this);
		}
		try 
		{
			//open message file and store it into messageList array
			try 			
			{
				fstream = new FileInputStream("messages.txt"); //opening message of day file
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader buf = new BufferedReader(new InputStreamReader(in));
							
				try 
				{
					while ((messageOfDay = buf.readLine()) != null)
					{
						String[] token = messageOfDay.split(delim);
						messageList.add(messageOfDay);	
					}
				}
				catch (IOException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace(); //if the message of day file is not found
			}
			// As long as we receive data, process the client's messages
			while ((line = in.readLine()) != null) 
			{
				String[] result = line.split(" ");			//parse the user's string into tokens
				for(int j = 0; j < handlers.size(); j++) 
				{	
					synchronized(handlers) 
					{
					ChildThread handler =(ChildThread)handlers.elementAt(j);
									
					if (result[0].contentEquals("LOGIN"))	//first token is checked to see if it is a valid command
						if(result.length==3)
						{
							for(int i=0;i<4;i++) 
							{
								if (users[0][i].contentEquals(result[1]) && users[1][i].contentEquals(result[2]))
								{
										if (handler == this) 
										{
											handler.out.println("200 OK");
											handler.out.flush();
										}
									
									userID=result[1];
									isLoggedIn=true;
									
									//check if user is root
									if (isLoggedIn&&userID.contentEquals("root"))
									{
										isRoot=true;
									}
								}
							}
						}
						else
						{
							if (handler == this) 
							{
								handler.out.println("410 Wrong UserID or Password");
								handler.out.flush();
								result[0]=" ";
							}
						}
					if (result[0].contentEquals("MSGGET"))
					{
						if (result.length==1)
						{
							if (handler == this) 
							{
								handler.out.println("200 OK"+"\n" + messageList.get(messCount));
								handler.out.flush();
								messCount++;
								result[0]=" ";
							}
							
						}
						
						//reset message counter to the beginning of the list	
						if (messCount > messageList.size()-1)
						{
							messCount = 0;
						}
					}
					if (result[0].contentEquals("MSGSTORE"))
						if ((result.length==1) && isLoggedIn)
						{
							if (handler == this) 
							{
								handler.out.println("200 OK");
								handler.out.flush();
							
								messageOfDay = in.readLine();
								messageList.add(messageOfDay);
							
								handler.out.println("200 OK");
								handler.out.flush();
								result[0]=" ";
							}
						}
						else
							if (handler == this) 
							{
								handler.out.println("401 You are not currently logged in, login first.");
								handler.out.flush();
							}							
					if (result[0].contentEquals("LOGOUT"))
						if (result.length==1 && isLoggedIn)
						{
							if (handler == this) 
								{
									handler.out.println("200 OK");
									handler.out.flush();
									result[0]=" ";
								}
							isLoggedIn = false;
							isRoot=false;
							userID="Unknown";
							
						}
					if (result[0].contentEquals("QUIT"))
						if (result.length==1)
						{
							if (handler == this) 
								{
									handler.out.println("200 OK");
									handler.out.flush();
									result[0]=" ";
								}
							isLoggedIn = false;
							isRoot=false;
							userID="";
							
						}
					
					
						// Broadcast it to everyone!  You will change this.  
						// Most commands do not need to broadcast
						/*for(int i = 0; i < handlers.size(); i++) 
						{	
							synchronized(handlers) 
							{
							ChildThread handler =
								(ChildThread)handlers.elementAt(i);
							if (handler != this) 
							{
								handler.out.println(line);
								handler.out.flush();
							}
							}
						}*/
						
					}
				}
				if (result[0].contentEquals("WHO"))		//get all the active user's IP and socket address
				{
					if (result.length==1)
					{
						os.println("200 OK");
		    				for(int i = 0; i < handlers.size(); i++)
							{	
		    					   synchronized(handlers)
								   {
									
		    						   ChildThread handler =(ChildThread)handlers.elementAt(i);
									   String active=handler.userID;
		    						   if (!active.contentEquals("Unknown"))
										{	
											os.println("List of active users:\n" + handler.userID + "    " + handler.socket.getRemoteSocketAddress());
											os.flush();
										} 
		    					   }
		    				}
					}
				}
				
				if (result[0].contentEquals("SEND"))
					if (result.length==2)
					{
						pMessage="";
						
						for(int j = 0; j < handlers.size(); j++) 			// check if there if the user is active
						{	
							synchronized(handlers) 
							{	
								ChildThread handler =(ChildThread)handlers.elementAt(j);
								String active=handler.userID;
								//os.println(active);
								if (result[1].contentEquals(active))
								{
									activeUser=true;
								}
								
							}
						}
						if (activeUser)										// if the user is active, send message to the specified user
						{	
							for(int j = 0; j < handlers.size(); j++) 
							{	
								synchronized(handlers) 
								{
									ChildThread handler =(ChildThread)handlers.elementAt(j);

									String active=(String)handler.userID;	
									if (result[1].contentEquals(active))
									{
										os.println("200 OK");
										pMessage = in.readLine();
										handler.out.println("200 OK you have a new message from " + userID + "\n" + userID + ": " + pMessage);
										handler.out.flush();
									}
									if (handler == this) 
									{
										handler.out.println("200 OK");
										handler.out.flush();
									}
								}
							}
							activeUser=false;
						}
						else
							for(int j = 0; j < handlers.size(); j++) 
							{	
								synchronized(handlers) 
								{
									ChildThread handler =(ChildThread)handlers.elementAt(j);
									if (handler == this) 
									{
										handler.out.println("420 either the user does not exist or the receiver is not logged in");
										handler.out.flush();
									}
								}
							}
							
					}
				if (result[0].contentEquals("SHUTDOWN"))
				{
					if (result.length==1)									//only the root user is allowed to carry out shutdown command
					{
						if (isRoot)
						{
							for(int j = 0; j < handlers.size(); j++) 
							{	
								synchronized(handlers) 
								{
									ChildThread handler =(ChildThread)handlers.elementAt(j);
									if (handler == this) 
									{
										handler.out.println("200 OK");
										handler.out.flush();
									}
								}
							}
							for(int i = 0; i < handlers.size(); i++)		//broadcast to all clients that server is shutting down
							{	
								synchronized(handlers) 
								{
									ChildThread handler =(ChildThread)handlers.elementAt(i);
									handler.out.println("210 the server is about to shutdown.....");
									handler.out.flush();
								}
							}
							System.exit(0);
						}
						else
						{
							os.println("402 User not allowed to execute this command");
						}
					}
				}
				System.out.println(line);
			}
		} 
		catch(IOException ioe) 
		{
			ioe.printStackTrace();
		} 
		finally 
		{
			try 
			{
				in.close();
				out.close();
				socket.close();
			} 
			catch(IOException ioe) 
			{
			} 
			finally 
			{
				synchronized(handlers) 
				{
					handlers.removeElement(this);
				}
			}
		}
  }

}

