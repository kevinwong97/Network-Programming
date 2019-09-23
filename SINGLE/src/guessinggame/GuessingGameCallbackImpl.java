package guessinggame;

import java.io.IOException;
import java.io.ObjectOutputStream;

import server.Response;

/**
 * Displays secret code in server console
 * implements gcb interface, responses are of type object, commandmsg and serialized, 
 * sends messages to client 
 * 
 *  @author Kevin W
 */
public class GuessingGameCallbackImpl implements GuessingGameCallback {
	/**
	 * send instance of client via stream
	 */
	private ObjectOutputStream stream;
	
	public GuessingGameCallbackImpl(ObjectOutputStream stream) {
		this.stream = stream;
	}
	
	private void respond(String message) {
		respond(message, Response.PRINTMESSAGE);
	}
	
	/**
	 * Sends instances of server.
	 * Response object to the client.
	 * 
	 */
	private void respond(String message, int responseType) {
		try {
			stream.writeObject(new Response(message, responseType));
		} catch (IOException e) {
			System.err.println("An error occured whilst attempting to send. " + e.getMessage());
		}
	}
	

     /**
	 * initiate game,
	 * sends message to client that game has initiated
	 */
	@Override
	public void gcbInitiate(GuessingGame game) {
		respond("The game has initiated");
	}
	
	
	/**
	 * generated secret code,
	 * This will prints the secret code in the server's console (not client)
	 */
	@Override
	public void gcbGeneratedSecretCode(GuessingGame game, String secretCode) {
		System.out.printf("New secret code generated (%s)\n", secretCode);
	}

	
	/**
	 * round begin. message client that the round has started
	 */
	@Override
	public void gcbRoundBegin(GuessingGame game, GuessingGameRound round, Player player) {
		respond("ROUND BEGIN");
	}

	/**
	 * player register, message client that it has registered the player
	 */
	@Override
	public void gcbPlayerRegistered(GuessingGame game, GuessingGameRound round, Player player) {
		respond(String.format("Player \"%s\" successfully registered.", player.getName()));
	}

	/**
	 * add guessed number, send back the number the client entered
	 */
	@Override
	public void gcbAddGuessNum(GuessingGameRound round, Player player, String guess) {
		respond("You guessed " + guess);
	}
	
	/**
	 * incorrect guess, message the client that the guess is incorect
	 * sends back incorrect guesses for each round
	 */
	@Override
	public void gcbIncorrectGuess(GuessingGameRound round, Player player, String guess) {
		respond(String.format("INCORRECT GUESS. Correct: %s, Incorrect: %s\n", 
			round.returnCorrectPos(guess),
			round.returnIncorrectPos(guess)
		));
		
	}
	

	/**
	 * palyer win, message the client that they won the round
	 * the client also gets a total tally of number of guesses for that round
	 */
	@Override
	public void gcbPlayerWin(GuessingGameRound round, Player player, int numOfGuesses) {
		respond("Congratulations. Number of guesses: " + numOfGuesses);
	}

	
	/**
	 * player lose, message the client that it has lot the round, but sends the answer for the secret code.
	 */
	@Override
	public void gcbPlayerLose(GuessingGameRound round, Player player, String secretCode) {
		respond(String.format("You lose. Secret code is %s.", secretCode));
	}

	
	/**
	 * round finish 
	 * message the client that it has finish the round
	 */
	@Override
	public void gcbRoundFinish(GuessingGame game, GuessingGameRound round) {
		respond("ROUND FINSHED");
	}

	/**
	 * player forfeit 
	 * message the player that it has forfeited the round.
	 */
	@Override
	public void gcbPlayerForfeit(GuessingGame game, GuessingGameRound round, Player player) {
		respond("You forfeited this game.");
	}
	
     /**
	 * player quit
	 * quit the client side of the game.
	 */
	@Override
	public void gcbPlayerQuit(GuessingGame game, Player player) {
		respond("Simple Guessing Game will now close", Response.QUIT);
	}
}