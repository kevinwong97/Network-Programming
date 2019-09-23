package guessinggame;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Implement gcb interface and logs the game, where each method has a private method 
 * @author Kevin W
 *
 */
public class GuessingGameCBLoggerImpl implements GuessingGameCallback {
	
	private Logger logger;
	

	public GuessingGameCBLoggerImpl(Logger logger) {
		this.logger = logger;
	}	

	/**
	 * initiates game
	 */
	@Override
	public void gcbInitiate(GuessingGame game) {
		GAME_INITIATED("GuessingGame initiated");
	}
	private void GAME_INITIATED(String message) {
		System.out.println("GAME INITIATED - " + message);
		logger.log(Level.INFO, message);
	}


	/**
	 * generated secret code
	 */
	@Override
	public void gcbGeneratedSecretCode(GuessingGame game, String secretCode) {
		GENERATE_SECRET_CODE("The generated secret code is " + secretCode);
	}
	private void GENERATE_SECRET_CODE(String message) {
		System.out.println("GENERATED SECRET CODE - " + message);
		logger.log(Level.INFO, message);
	}


	/**
	 * round begin, logging all players in list
	 */
	@Override
	public void gcbRoundBegin(GuessingGame game, GuessingGameRound round, Player player) {
		String response = "New round started.\nPlayers:\n";
		
		for (int i = 0; i < round.getPlayers().size(); i++) {
			response += String.format("%d. %s\n", i + 1, round.getPlayers().get(i).getName());
		}
		
		ROUND_BEGIN(response);
	}
	private void ROUND_BEGIN(String message) {
		System.out.println("ROUND BEGIN - " + message);
		logger.log(Level.INFO, message);
	}


	/**
	 * player register
	 */
	@Override
	public void gcbPlayerRegistered(GuessingGame game, GuessingGameRound round, Player player) {
		PLAYER_REGISTERED(String.format("Player %s registered.\n", player.getName()));
	}
	private void PLAYER_REGISTERED(String message) {
		System.out.println("PLAYER REGISTERED - " + message);
		logger.log(Level.INFO, message);
	}
	

	/**
	 * add guessed number
	 */
	@Override
	public void gcbAddGuessNum(GuessingGameRound round, Player player, String guess) {
		GUESS_ADDED(String.format("Player %s guessed %s\n", player.getName(), guess));
	}
	private void GUESS_ADDED(String message) {
		System.out.println("ADDED GUESS - " +  message);
		logger.log(Level.INFO, message);
	}


	/**
	 * incorrect guess
	 */
	@Override
	public void gcbIncorrectGuess(GuessingGameRound round, Player player, String guess) {
		GUESSES_NUM_INCORRECT(String.format("Player %s guesses incorrectly\n. Correct: %s, Incorrect: %s\n", 
			player.getName(),
			round.returnCorrectPos(guess),
			round.returnIncorrectPos(guess)
		));
	}
	private void GUESSES_NUM_INCORRECT(String message) {
		System.out.println("INCORRECT GUESS - " +  message);
		logger.log(Level.INFO, message);
	}


	/**
	 * player win
	 */
	@Override
	public void gcbPlayerWin(GuessingGameRound round, Player player, int numOfGuesses) {
		PLAYER_WIN(String.format("Player %s has won with %d guesses\n", player.getName(), numOfGuesses));
	}
	private void PLAYER_WIN(String message) {
		System.out.println("PLAYER WIN - " +  message);
		logger.log(Level.INFO, message);
	}


	/**
	 * player lose
	 */
	@Override
	public void gcbPlayerLose(GuessingGameRound round, Player player, String secretCode) {
		PLAYER_LOSE(String.format(
			"Player %s lost the round. The generated secret code was %s\n", player.getName(), secretCode));
	}
	private void PLAYER_LOSE(String message) {
		System.out.println("PLAYER LOSE - " +  message);
		logger.log(Level.INFO, message);
	}


	/**
	 * round finish
	 */
	@Override
	public void gcbRoundFinish(GuessingGame game, GuessingGameRound round) {
		
		String response = "Round ended. \n";
		
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
			response = response.concat(" * There are no winners for this round *\n");
		}
		
		// losers
		response += "LOSERS: \n";
		if (round.getLosers().size() > 0) {
			for (Player player : round.getLosers()) {
				response += String.format(" - %s\n", player.getName());
			}
		} else {
			response += " * There are no losers for this round *\n";
		}
		
		// forfeiters
		response += "FORFEITERS: \n";
		if (round.getForfeiters().size() > 0) {
			for (Player player : round.getForfeiters()) {
				response += String.format("- %s\n", player.getName());
			}
		} else {
			response += " * There are no players forfeited for this round *";
		}
		
		response += "\n";
		
		
		ROUND_FINISH(response);
	}
	private void ROUND_FINISH(String message) {
		System.out.println("ROUND FINISH - " +  message);
		logger.log(Level.INFO, message);
	}
	

	/**
	 * player forfeits
	 */
	@Override
	public void gcbPlayerForfeit(GuessingGame game, GuessingGameRound round, Player player) {
		PLAYER_FORFEITED(String.format("Player %s forfeited the round.\n", player.getName()));
	}
	
	private void PLAYER_FORFEITED(String message) {
		System.out.println("PLAYER FORFEITED - " +  message);
		logger.log(Level.INFO, message);
	}
	

	/**
	 * player quit
	 */
	@Override
	public void gcbPlayerQuit(GuessingGame game, Player player) {
		PLAYER_QUIT(String.format("Player %s quit the game.\n", player.getName()));
	}
	
	private void PLAYER_QUIT(String message) {
		System.out.println("PLAYER QUIT - " +  message);
		logger.log(Level.INFO, message);
	}

}
