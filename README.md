multithreading
==============

Program of a multithreaded server using java thread and a client that can monitor server's message and user's input

To run the programs:
		Type "make" while in the current directory where the files are saved
		Type "java MultiThreadedServer"
		Type "java Client <IP address>" in a new terminal
		
The following commands implemented:
		MSGGET-displays message of the day
		MSGSTORE-stores a new message of the day
		SHUTDOWN-closes all server and clients
		LOGIN-login to server
		LOGOUT-logout of server
		QUIT-quit server
		WHO-display the list of active users
		SEND- send private message to active user
		
Currently no Known bugs.

Sample Output on Server:
		chenaj@cluster2:~/CIS427p2$ java MultiThreadServer
		MSGGET
		MSGSTORE
		LOGIN john john01
		MSGSTORE
		My Message!
		WHO
		SHUTDOWN
		LOGOUT
		
Sample Output on Client:
		chenaj@cluster2:~/CIS427p2$ java Client 141.215.10.31
		MSGGET
		200 OK
		Hello! How are you?
		MSGSTORE
		401 You are not currently logged in, login first.
		LOGIN john john01
		200 OK
		MSGSTORE
		200 OK
		My message!
		200 OK
		WHO
		200 OK
		List of active users:
		root   /141.215.10.31:52750
		mary   /141.215.10.31:48590
		SHUTDOWN
		402 User not allowed to execute this command
		LOGOUT
		200 OK

(c) Angela Chen
