package guessinggame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import guessinggame.Player.PlayerStatus;

/**
 * the game round
 * 
 * @author Kevin W
 *
 */
public class GuessingGameRound {
	
	public static final int MAX_GUESSES = 4;
	private String secretCode;
	private boolean checkFinished = false;
	List<Player> players = new ArrayList<>();
	List<Player> winners = new ArrayList<>();
	List<Player> losers = new ArrayList<>();
	List<Player> forfeited = new ArrayList<>();
	List<String> guesses = new ArrayList<>();
	
	
	public GuessingGameRound(String secretCode) {
		this.secretCode = secretCode;
	}
	
	/**
	 * add player to the round and clear their guesses
	 */
	public void addPlayer(Player player) {
		player.resetAllPlayerGuesses();
		players.add(player);
	}
	
	public String getSecretCode() {
		return secretCode;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<String> getGuesses() {
		return guesses;
	}
		
	/**
	 * adding guesses based on win, lose or forfeit
	 */
	public void addGuess(Player player, String guess) {
		// dont need to add guesses when win or lose occurs
		if (checkWinner(player) || checkLoser(player)) {
			return;
		}
		
		// add guess if number of current guesses is less than max guesses
		if (players.contains(player) && player.getTotalGuesses() < MAX_GUESSES) {
			player.addGuess(guess);
			guesses.add(guess);
			//callbacks.forEach(call -> call.gcbAddGuessNum(this, player, guess));
		}
		
		// if guess equals secret code then player wins
		if (checkGuessEqual(guess)) {
			addWinner(player);
			//callbacks.forEach(call -> call.gcbPlayerWin(this, player, player.getTotalGuesses()));
			return;
		}
		
		// if the 4th present guess is incorrect, player loses
		if (player.getTotalGuesses() >= MAX_GUESSES && checkGuessEqual(guess) == false) {
			addLoser(player);
			//callbacks.forEach(call -> call.gcbPlayerLose(this, player, secretCode));
			return;
		}
	}
	
	/**
	 * add winner if they are registered in player list, have not lost yet and not won yet.
	 */
	public void addWinner(Player player) {
		if (players.contains(player) && winners.contains(player) == false && losers.contains(player) == false) {
			winners.add(player);
		}
	}
	
	/**
	 * add loser if they are registered in player list, have not lost yet and not won yet.
	 */
	public void addLoser(Player player) {
		if (players.contains(player) && losers.contains(player) == false && losers.contains(player) == false) {
			losers.add(player);
		}
	}
	
	public boolean checkWinner(Player player) {
		return winners.contains(player);
	}
	
	public boolean checkLoser(Player player) {
		return losers.contains(player);
	}
	
	public boolean checkGuessEqual(String guess) {
		return secretCode.equals(guess);
	}
	
	/**
	 * get player by name in player list
	 */
	public Player getPlayerByName(String playerName) {
		for (Player player : players) {
			if (player.getName().equals(playerName)) {
				return player;
			}
		}	
		return null;
	}
	
	
	/**
	 * Adds the player to the list of forfeiters for this round.
	 * @param player
	 */
	private void addForfeiter(Player player) {
		if (players.contains(player) && !losers.contains(player) && !winners.contains(player)) {
			forfeited.add(player);
		}
	}
	
	
	/**
	 * check if player is in the list of forfeited players
	 */
	public boolean checkForfeit(Player player) {
		return forfeited.contains(player);
	}
	
	public void forfeit(Player player) {
		for (int i = player.getTotalGuesses(); i < MAX_GUESSES + 1; i++) {
			player.addGuess("");
		}
		addForfeiter(player);
	}
	
	public synchronized List<Player> getWinners() {
		return winners;
	}
	
	public List<Player> getLosers() {
		return losers;
	}
	
	public List<Player> getForfeiters() {
		return forfeited;
	}
	
	public boolean checkFinished() {
		return checkFinished;
	}
	
	public void finish() {
		for(Player player : players) {
			// non-winners are now losers to end the game
			if (this.checkWinner(player) == false) {
				addLoser(player);
			}
		}
		this.checkFinished = true;
	}
	
    /**
	 * check if guess equals secret code
	 * if a number found from guess the number and in the correct position
	 */
	public int returnCorrectPos(String guess) {
		int correctPos = 0;
		
		for (int i = 0; i < guess.length(); i++) {
			if (i > secretCode.length() - 1) {
				break;
			}	
			char guessNum = guess.charAt(i);
			char secretCodeNum = secretCode.charAt(i);
			if (guessNum == secretCodeNum) {
				correctPos += 1;
			}
		}
		
		return correctPos;
	}
	
	/**
	 * check if guess equals secret code
	 * if a number found in incorrect position returns incorrect positions
	 */
	public int returnIncorrectPos(String guess) {
		int incorrectPos = 0;
		
		for (int i = 0; i < guess.length(); i++) {	
			if (i > secretCode.length() - 1) {
				break;
			}
			char guessNum = guess.charAt(i);
			char secretCodeNum = secretCode.charAt(i);
			if (guessNum == secretCodeNum) {
				continue;
			}
			else if (secretCode.contains(new String(new char[] { guessNum }))) {
				incorrectPos += 1;
			}
		}
		return incorrectPos;
	}
}
