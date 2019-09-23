package guessinggame;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import server.Response;
import server.ServerCallback;
import server.ServerProcess;


/** 
 * 
 * Displays secret code in server console
 * implements gcb interface, responses are of type object, responses and serialized, 
 * sends messages to client 
 * 
 * @author Kevin W
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
		respond(message, Response.PRINT_MESSAGE);
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
		String response = "----------------ROUND BEGIN----------------\n";
		for (Player p : round.getPlayers()) {
			response += String.format(" - %s", p.getName());
			if (p == player) {
				response += "(you)";
			}
			
			response += "\n";
		}
		
		response += "\n";
		
		respond(response);

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
	 * player win, message the client that they won the round
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
		String response = "----------------ROUND FINISH----------------\n";
		
	
		// using a new reference, we can sort the winners  
		List<Player> winners = round.getWinners().stream().filter(w -> w != null).collect(Collectors.toList());
		winners.sort(new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				return o1.compareTo(o2);
			}
		});
		
		// winners
		response += "WINNERS: \n";
		if (round.getWinners().size() > 0) {
			for (int i = 0; i < winners.size(); i++) {
				response += String.format(" %d. %s (%d guesses)\n", i + 1,  winners.get(i).getName(), winners.get(i).getTotalGuesses());
			}
		} else {
			response = response.concat(" - There are no winners for this round -\n");
		}
		
		// losers
		response += "LOSERS: \n";
		if (round.getLosers().size() > 0) {
			for (Player player : round.getLosers()) {
				response += String.format(" - %s\n", player.getName());
			}
		} else {
			response += " - There are no losers for this round -\n";
		}
		
		// forfeiters
		response += "FORFEITERS: \n";
		if (round.getForfeiters().size() > 0) {
			for (Player player : round.getForfeiters()) {
				response += String.format("- %s\n", player.getName());
			}
		} else {
			response += " - There are no players forfeited for this round -";
		}
		
		response += "\n";
		
		respond(response);
	}

	/**
	 * player forfeits
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
