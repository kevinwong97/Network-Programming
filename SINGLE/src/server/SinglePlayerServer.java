package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import guessinggame.GuessingGame;
import guessinggame.GuessingGameCallback;
import guessinggame.GuessingGameCBLoggerImpl;

/**
 * server accepts 1 client at at time, player guesses secret code to win. 
 * game is player in rounds, player needs to enter their name to join, then followed
 * with entering a number to guess what number the secret code is, which is generated as
 * soon as the game round st arts. player has 4 guesses before the round is over for them.
 * after a round, user can decide to continue or quit. 
 * 
 * @author Kevin W
 *
 */
public class SinglePlayerServer {
	
	public static final int SERVER_PORT = 61231;
	public static final String GAME_LOG = "game.log";
	public static final String COMMUNICATIONS_LOG = "communications.log";
	
	private int portNum;
	private GuessingGame game;
	
	private List<ServerCallback> serverCallbacks = new ArrayList<>();
	private List<GuessingGameCallback> gameCallbacks = new ArrayList<>();
	private ServerSocket serverSocket;
	
	public SinglePlayerServer(int portNum, GuessingGame game) {
		this.portNum = portNum;
		this.game = game;
	}
	
	/**
	 * initiate the server. 
	 */
	public void initiate() throws IOException {
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
	 * If closing the ServerSocket throws an Exception, 
	 * it will call all serverCallbacks' scbException() method
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
	public static void main(String[] args) {
		
		int portNum = getPortNum(args);
		GuessingGame game = new GuessingGame();
		
		
		// Communication logs are done by Servercallback interface
		Logger commLog = LoggerFileHandler.createLogger(COMMUNICATIONS_LOG, ServerCallbackImpl.class);
		ServerCallback serverCallback = new ServerCallbackImpl(commLog);
		
		// GuessingGame logs will be handled by GuessingGameCBLoggerImpl 
		Logger gameLog = LoggerFileHandler.createLogger(GAME_LOG, GuessingGameCBLoggerImpl.class);
		GuessingGameCBLoggerImpl gameCallbackLogger = new GuessingGameCBLoggerImpl(gameLog);
		
		SinglePlayerServer singlePlayerServer = new SinglePlayerServer(portNum, game);
		singlePlayerServer.addServerCallback(serverCallback);
		
		try {
     		singlePlayerServer.initiate();
			
			ServerSocket serverSocket = singlePlayerServer.getServerSocket();
			
			// server will accept new clients until killed by server user
			do {
				
				Socket socket = serverSocket.accept();
				
				// keep alive
				socket.setKeepAlive(true);
				
				serverCallback.scbClientConnected(singlePlayerServer, socket);
				
				// launch the game handler
				ServerProcess process = new ServerProcess(game, socket, serverCallback, gameCallbackLogger);
				process.begin();
				
			} while (true);
			
		}
		catch (IOException e) {
		    // exceptions are logged using callbacks
			serverCallback.scbException(null, e);
		}
		finally {
			singlePlayerServer.close();
		}
		
	}
	
	
	/**
	 * gets portNum number from command line arg and 
	 * throws exception if not a number
	 */
	public static int getPortNum(String[] args) {
		int portNum = SERVER_PORT;
		
		if (args.length < 1) {
			return portNum;
		}
		
		try {
			portNum = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException numFormatEx) {
			System.err.printf("%s is not a valid portNum number", args[0]);
		}
		
		return portNum;
	}

}
