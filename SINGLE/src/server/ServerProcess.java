package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import guessinggame.GuessingGame;
import guessinggame.GuessingGameCallback;
import guessinggame.GuessingGameCallbackImpl;
import guessinggame.GuessingGameCBLoggerImpl;
import guessinggame.GuessingGameRegulator;

/**
 * Server outlines the game process regulations 
 * The game use serialization and ObjectOutputStream. This allows 
 * for multiline printing in the client side and it allows for sendings commands to client.
 * 
 * @author Kevin W
 *
 */
public class ServerProcess {
	

	private GuessingGame game;
	private Socket socket;
	private ServerCallback callb;
	private ObjectOutputStream stream;
	private BufferedReader reader;
	private GuessingGameRegulator manager;
	
	public ServerProcess(GuessingGame game, Socket socket, ServerCallback callback, GuessingGameCBLoggerImpl gameLoggerCallback) throws IOException {
		this.game = game;
		this.socket = socket;
		this.callb = callback;
		
		// serialize the response, so use ObjectOutputStream
		this.stream = new ObjectOutputStream(socket.getOutputStream());
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.manager = new GuessingGameRegulator(game);
		
		GuessingGameCallback callb = new GuessingGameCallbackImpl(stream);
		manager.addCB(callb);
	
		manager.addCB(gameLoggerCallback);
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public GuessingGame getGame() {
		return game;
	}
	
	public GuessingGameRegulator getGameManager() {
		return manager;
	}
	
	
	/**
	 * ask for player name, initiate the round, ask user to enter their guess number, 
	 * loop until win, lose, forfeit
	 * round ends, ask player to whether they want to quit or cont.
	 */
	public void begin() {
		
		try {
			
			if (manager.getPresentPlayer() == null) {
				String name = readline("Enter your name: ", true);
				manager.setPresentPN(name);
			}
			
			manager.beginTheNextRound();
			
			do {
				String guess = readline("Enter your guess: ");
				manager.addGuess(guess);
				
				if (manager.checkPresentPlayerForfeit()) {
					break;
				}
				
				if (manager.checkPlayerWin() || manager.checkPlayerLose()) {
					break;
				}
				
			} while (true);
			manager.finishPresentRound();	
			boolean shouldContinue = continueOrQuit("Press (p) to continue to play, or (q) to quit: ");
			
			if (!shouldContinue) {
				manager.playerQuited();
			} else {
				
				// loop the whole process again
				begin();
			}
			
		} catch (IOException e) {
			callb.scbException(this, e);
		} catch (Exception e) {
			callb.scbException(this, e);
		} finally {
			close();
		}
	}
	
	/**
	 * prompt user continue or quit
	 * loop until 'p' or 'q' is entered
	 * 
	 */
	public boolean continueOrQuit(String message) throws IOException {
		String reply = readline(message, true);
		reply = reply.trim().toLowerCase();
		if (reply.equals("p")) {
			return true;
		}
		else if (reply.equals("q")) {
			return false;
		}
		else {
			sendError("Please enter 'p' or 'q'.");
			return continueOrQuit(message);
		}
	}
	
	/**
	 * Close connection
	 */
	public void close() {
		manager.removePresentPlayer();
		
		try {
			if (stream != null) stream.close();
			if (reader != null) reader.close();
			if (socket != null) socket.close();
			
			callb.scbClientDisconnected(null, socket, this);
			
		} catch (IOException e) {
			callb.scbException(this, e);
		}
	}
	
	
	/**
	 * Reply messages to the client. 
	 * Use serialization to send objects to the client. 
	 * The client then unlocks the details of the serialized object
	 */
	private void reply(String message) throws IOException {
		Response response = Response.message(message);
		stream.writeObject(response);
		callb.scbSendResponse(this, response);
	}
	
	private String readline(String message) throws IOException {
		return readline(message, false);
	}

	
	/**
	 * asks users for input. 
	 * pass a message as first arg, and boolean value 
	 * for the second argument 
	 * If the client sends an empty string, method will be called and loop 
	 * until input sent is not empty 
	 */
	private String readline(String message, boolean isRequired) throws IOException {
		Response response = Response.readLine(message);
		stream.writeObject(response);
		callb.scbSendResponse(this, response);
		
		String line = reader.readLine();
		callb.scbClientReply(this, line);
		
		if (isRequired && line.trim().isEmpty()) {
			sendError("Please enter non empty input.");
			return readline(message, isRequired);
		}
		
		return line.trim();
	}

	
	/**
	 * Send message to client"
	 */
	private void sendError(String message) throws IOException {
		Response response = Response.message("ERROR: " + message);
		stream.writeObject(response);
		callb.scbSendResponse(this, response);
	}
	
	/**
	 * Read number from client, if not a number then ask again.
	 * 
	 */
	private int readInt(String message) throws IOException {
		String reply = readline(message, true);
		int num = 0;
		
		try {
			num = Integer.parseInt(reply.trim());
			return num;
		} catch (NumberFormatException e) {
			sendError(reply + " is not a valid number");
			return readInt(message);
		}
	}
}
