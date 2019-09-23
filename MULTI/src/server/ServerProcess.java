package server;

import java.net.Socket;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;

import guessinggame.GuessingGame;
import guessinggame.GuessingGameCallback;
import guessinggame.GuessingGameCallbackImpl;
import guessinggame.GuessingGameCBLoggerImpl;
import guessinggame.GuessingGameRegulator;

/**
 * The class runs a separate thread and syncs with other threads running this class.
 * The game use serialization and ObjectOutputStream. This allows 
 * for multiline printing in the client side and it allows for sendings commands to client.
 * @author Kevin W
 *
 */
public class ServerProcess implements Runnable {
	
	/**
	 * RESTRICTED object share among all threads using this class
	 */
	public static final Object RESTRICTED = new Object();
	
   /**
	* 30 seconds waiting time
	*/
	public static final int WAITING_TIME_SECONDS = 30 * 1000;

	private GuessingGame game;
	private Socket socket;
	private ServerCallback callb;
	private ObjectOutputStream stream;
	private BufferedReader reader;
	private GuessingGameRegulator regulator;
	
	
	public ServerProcess(GuessingGame game, Socket socket, ServerCallback callback, GuessingGameCBLoggerImpl gameLoggerCallback) throws IOException {
		this.game = game;
		this.socket = socket;
		this.callb = callback;
		this.stream = new ObjectOutputStream(socket.getOutputStream());
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.regulator = new GuessingGameRegulator(game);
		
		GuessingGameCallback callb = new GuessingGameCallbackImpl(stream);
		regulator.addCB(callb);
		regulator.addCB(gameLoggerCallback);
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public GuessingGame getGame() {
		return game;
	}
	
	public GuessingGameRegulator getGuessingGameRegulator() {
		return regulator;
	}
	
	
	@Override
	public void run() {
		
		try {
			
			// ask for player name
			if (regulator.getPresentPlayer() == null) {
				String name = readline("Enter your name: ", true);
				regulator.setPresentPN(name);
			}
			
			// synchronized block and notifies players waiting
			synchronized (RESTRICTED) {
				
                // 3rd player does not enter this
				// if there is no 3rd player within 20 seconds, wait() will return
				if (regulator.getNumPlayers() < GuessingGame.MIN_PLAYERS) {
					reply("Waiting for other players...");
					RESTRICTED.wait(WAITING_TIME_SECONDS);
				}
				
				// only the 3rd and the next players will enter this block of code
				// it notifies other waiting players, they joined
				if (regulator.getNumPlayers() >= GuessingGame.MIN_PLAYERS) {
					RESTRICTED.notifyAll();
				}
			}
			
			
			// only 1 person allowed to initiates a new round
			synchronized (RESTRICTED) {
				regulator.beginTheNextRound();
		        // join the round that has begun
				regulator.joinPresentPlayer();
			}
			
			
			do {
				
				String guess = readline("Enter your guess: ");
				regulator.addGuess(guess);
				
				// Player forfeits if it enters 'f'
				if (regulator.checkPresentPlayerForfeit()) {
					break;
				}
				
				if (regulator.checkPlayerWin() || regulator.checkPlayerLose()) {
					break;
				}
				
			} while (true);
			
			
			synchronized (RESTRICTED) {
				// if round has not finished wait for others to finish until  win, lose, forfeit
				if ( ! regulator.checkPresentRoundFinished()) {
					reply("Wait for other players to finish...");
					RESTRICTED.wait();
				}
				else {
					// notify threads that it is finished
					RESTRICTED.notifyAll();
				}
			}
			// game round is finished
			regulator.finishCurrentRound();
					
			// prompt users continue or quit
			boolean shouldContinue = continueOrQuit("Press (player) to continue to play, or (q) to quit: ");
			
			synchronized (RESTRICTED) {
				
				// player quits, notify all players that they quit
				if (!shouldContinue) {
					regulator.playerQuited();
					RESTRICTED.notifyAll();
				}
				else {
					regulator.checkContinue();
				
					// The players that are finished will enter this block, players that have not finished will not
					if ( !regulator.checkPresentRoundFinished() || !regulator.isAllOtherPlayersChosenToContinueOrQuit() ) {
						reply("Please wait for other players to finish before next round begins...");
						RESTRICTED.wait();
					}
					
					// last player runs this
					if ( regulator.checkPresentRoundFinished() && regulator.isAllOtherPlayersChosenToContinueOrQuit() ) {
						RESTRICTED.notifyAll();
					}
				}
			}
			
		     
			if (shouldContinue) {
				run(); //loop again if player choose continue
			}
				
		} 
		// in case an IOException occurred, fire scbException() event
		catch (IOException e) {
			callb.scbException(this, e);
		} 
		// in case an Exception occurred, fire scbException() event
		catch (Exception e) {
			callb.scbException(this, e);
		} 
		// close the process
		finally {
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
		if (reply.equals("player")) {
			return true;
		}
		else if (reply.equals("q")) {
			return false;
		}
		else {
			sendError("Please enter 'player' or 'q'.");
			return continueOrQuit(message);
		}
	}

	/**
	 * Close connection
	 */
	public void close() {
		regulator.removePresentPlayer();
		
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
	
	private boolean yesOrNo(String message) throws IOException  {
		String response = "";
		
		response = readline(message);
		response = response.toLowerCase();
		
		if (response.equals("y") || response.equals("yes")) {
			return true;
		}
		else if (response.equals("n") || response.equals("no")) {
			return false;
		}
		else {
			sendError(response + " is not a valid response.");
			return yesOrNo(message);
		}
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
