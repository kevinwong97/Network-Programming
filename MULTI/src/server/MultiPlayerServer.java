package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import guessinggame.GuessingGame;
import guessinggame.GuessingGameCallback;
import guessinggame.GuessingGameCallbackImpl;
import guessinggame.GuessingGameCBLoggerImpl;
import guessinggame.GuessingGameRegulator;

/**
 * Server handles up to 6 clients, and is a simple guessing number game between
 * players with a maximum of 4 guesses
 * @author Kevin W
 *
 */
public class MultiPlayerServer {
	
	public static final int SERVER_PORT = 61231;
	public static final String GAME_LOG = "game.log";
	public static final String COMMUNICATIONS_LOG = "communications.log";
	
	private int portNum;
	private GuessingGame game;
	private List<ServerCallback> serverCallbacks = new ArrayList<>();
	private List<GuessingGameCallback> gameCallbacks = new ArrayList<>();
	private ServerSocket serverSocket;
	
	
	public MultiPlayerServer(int portNum, GuessingGame game) {
		this.portNum = portNum;
		this.game = game;
	}
	
	public void start() throws IOException {
		serverSocket = new ServerSocket(portNum);
		serverCallbacks.forEach(call -> call.scbServerInitiated(this, portNum));
	}
	
	/**
	 * add a server callback
	 */
	public void addServerCallback(ServerCallback callback) {
		serverCallbacks.add(callback);
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public GuessingGame getGame() {
		return game;
	}
	
	public List<GuessingGameCallback> getGameCallbacks() {
		return gameCallbacks;
	}
	
	public List<ServerCallback> getServerCallbacks() {
		return serverCallbacks;
	}
	
	
	/**
	 * Closes the ServerSocket
	 * If closing the ServerSocket throws an Exception
	 */
	public void close() {
		try {
			if (serverSocket != null) serverSocket.close();  
		} catch (IOException e) {
			serverCallbacks.forEach(call -> call.scbException(null, e));
		}
	}

   /**
	* main method 
	*/
	public static void main(String[] args) throws IOException {
		
		GuessingGame game = new GuessingGame();
		
		// Communication logs are done by Servercallback interface
		Logger commLog = LoggerFileHandler.createLogger(COMMUNICATIONS_LOG, ServerCallbackImpl.class);
		ServerCallback serverCallback = new ServerCallbackImpl(commLog);
		
		// Game logs will be handled by GuessingGameCBLoggerImpl 
		Logger gameLog = LoggerFileHandler.createLogger(GAME_LOG, GuessingGameCBLoggerImpl.class);
		GuessingGameCBLoggerImpl gameCallbackLogger = new GuessingGameCBLoggerImpl(gameLog);
		
		// Client processes are saved in a hashmap
		Map<Socket, ServerProcess> processes = new ConcurrentHashMap<>();
		
		// Establish server
		MultiPlayerServer multiPlayerServer = new MultiPlayerServer(SERVER_PORT, game);
		multiPlayerServer.addServerCallback(serverCallback);
		
		try {
			multiPlayerServer.start();
			ServerSocket serverSocket = multiPlayerServer.getServerSocket();
			game.start();

			// close the server with 'exit'
			Thread background = new Thread(() -> {
				Scanner scanner = new Scanner(System.in);
				String line = "";
				System.out.println("Type command 'exit' to exit the server.");
				do {
					line = scanner.nextLine();
				} while (!line.equals("exit"));
				
				try {
					for (ServerProcess process : processes.values()) {
						try {
							Socket socket = process.getSocket();
							socket.close();
							serverCallback.scbClientDisconnected(multiPlayerServer, socket, process);
						} catch (IOException e) {
							serverCallback.scbException(process, e);
						}
					}
					serverSocket.close();
				} catch (IOException e) {
					serverCallback.scbException(null, e);
				}
				scanner.close();
			});
			background.start();
			
			
			do {
				// listening for oncoming connections
				Socket socket = serverSocket.accept();
				
				// keep alive
				socket.setKeepAlive(true);
				serverCallback.scbClientConnected(multiPlayerServer, socket);
				
				// process the game in a new thread for each client connected
				ServerProcess process = new ServerProcess(game, socket, serverCallback, gameCallbackLogger);
				Thread thread = new Thread(process);
				thread.start();
				
				// put this in map to reference if needed
				processes.put(socket, process);

			} 
			while (true);
		}
		// server close message
		catch (SocketException e) {
			System.out.println("Server closed.");
		}
		// other exception
		catch (IOException e) {
			serverCallback.scbException(null, e);
		}
		// server close
		finally {
			multiPlayerServer.close();
		}
	}

}
