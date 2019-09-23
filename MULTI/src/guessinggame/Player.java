package guessinggame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Player status of players in the game
 * @author Kevin W
 */
public class Player implements Comparable<Player> {
	
	public enum PlayerStatus {
		NOT_STARTED,
		STARTED,
		PLAYING,
		WON,
		LOST,
		FORFEITED,
		CHOSEN_TO_CONTINUE,
		QUITED
	}
	
	private String name;
	private List<String> guesses = new ArrayList<>();
	private String recentGuess = null;
	private PlayerStatus status = PlayerStatus.NOT_STARTED;
	
	public Player(String name) {
		this.name = name;
	}

	// used to reset Guesses before the initiates of each round.
	public void resetGuesses() {
		guesses = new ArrayList<>();
		recentGuess = null;
	}
	
	public PlayerStatus getStatus() {
		return status;
	}

	public void setStatus(PlayerStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}
	
	public List<String> getGuesses() {
		return guesses;
	}
	
	public String getRecentGuess() {
		return recentGuess;
	}
	
	/**
	 * Last guess is updated when addGuess() is called
	 */
	public void addGuess(String guess) {
		guesses.add(guess);
		recentGuess = guess;
	}
	
	public int getTotalGuesses() {
		return guesses.size();
	}
	
	/**
	 * clear all guesses
	 */
	public void resetAllPlayerGuesses() {
		guesses.clear();
	}
	
	public boolean checkWin(GuessingGameRound round) {
		return round.checkWinner(this);
	}
	
	public boolean checkLose(GuessingGameRound round) {
		return round.checkLoser(this);
	}
	
	public boolean checkForfeit(GuessingGameRound round) {
		return round.checkForfeit(this);
	}

	@Override
	public int compareTo(Player o) {
		return getTotalGuesses() - o.getTotalGuesses();
	}
}
