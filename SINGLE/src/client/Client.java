package client;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import server.Response;

/**
 * Class the represent the player's client machine. This client expects to see serialize form of the server
 * 
 * @author Kevin W
 */
public class Client {
	
	// you can use custom serverAddress by passing arg into main(String[] args)
	public static final String SERVER_ADDRESS = "localhost";
	
	// you can customize portNum by passing the second argument into the main(String[] args)
	public static final int SERVER_PORT = 61231;
	
	// timeout in ms (60000ms = 60s)
	public static final int TIME_OUT = 60000;


	public static void main(String[] args) {
		
		int portNum = getPortNumber(args);
		String serverAddress = getHostAddress(args);
		
		Socket socket = null;
		ObjectInputStream objectIS = null;
		PrintWriter pWriter = null;
		BufferedReader bfConsole = null;
		
		try {
			
			// By default this connects to 127.0.0.1:61231
			socket = new Socket();
			socket.connect(new InetSocketAddress(serverAddress, portNum), 60000);
			socket.setSoTimeout(60000);
			
			System.out.println("Connected to " + serverAddress + " on portNum" + portNum);

			objectIS = new ObjectInputStream(socket.getInputStream());
			pWriter = new PrintWriter(socket.getOutputStream(), true);
			bfConsole = new BufferedReader(new InputStreamReader(System.in));
			
			String line;
			Response command;
			
			do {
				command = (Response) objectIS.readObject();
				// print msg and read from console
				if (command.getType() == Response.READLINE) {
					System.out.print(command.getMessage());
					line = bfConsole.readLine();
					pWriter.println(line);
				}
				else {
					System.out.println(command.getMessage());
				}
			}
			
			// QUIT command. exit the loop
			while(command.getType() != Response.QUIT);
			
		}

		// Remember, compile the whole project
		catch (ClassNotFoundException e) {
			System.err.println("The command data sent by the server cannot be read by this client. " + e.getMessage());
		}
		// server has not initiated
		catch (UnknownHostException e) {
			System.err.printf("Server %s:%d cannot be found\n", serverAddress, portNum);
		}
		// Client timeout
		catch (SocketTimeoutException e) {
			System.err.println("Client reached timeout.");
		}
		// disconnect
		catch (EOFException e) {
			System.err.print("Client disconnected. ");
		}
		// IO exception
		catch (IOException e) {
			System.err.println("An error has occured, " + e.getMessage());
		}
		finally {
			try {
				if (bfConsole != null) bfConsole.close();
				if (pWriter != null) pWriter.close();
				if (objectIS != null) objectIS.close();
				if (socket != null) socket.close();
				
				System.out.println("Connection closed.");
			}
			catch (IOException e) {
				System.err.println("An error occured while closing connection. " + e.getMessage());
			}
		}
		
		
		

	}
	
	/**
	 * takes portNum as an argument in the second argument
	 * of the function
	 */
	public static String getHostAddress(String[] args) {
		String address = SERVER_ADDRESS;
		
		if (args.length < 1) {
			return address;
		}
		
		address = args[0];
		return address;
	}
	
	
	/**
	 * takes serverAddress as an argument in the first argument
	 * of the function
	 */
	public static int getPortNumber(String[] args) {
		int portNumber = SERVER_PORT;
		
		if (args.length < 2) {
			return portNumber;
		}
		
		try {
			portNumber = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException numFormatEx) {
			System.err.printf("This port number %s is unavailable", args[0]);
		}
		
		return portNumber;
	}
}