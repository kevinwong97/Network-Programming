package guessinggame;

import java.util.ArrayList;
import java.util.List;

/**
 * the game round
 * 
 * @author Kevin W
 *
 */
public class GuessingGameRound {
	
	public static final int MAX_GUESSES = 4;
	private String secretCode;
	private Player player;
	private boolean getPlayerWin = false;
	private boolean getPlayerLose = false;
	private boolean getPlayerForfeit = false;
	
	List<String> guesses = new ArrayList<>();
	private boolean checkFinished = false;
	

	public GuessingGameRound(String secretCode) {
		this.secretCode = secretCode;
	}
	
	public boolean getPlayerWin() {
		return getPlayerWin;
	}
	public void setPlayerWon(boolean getPlayerWin) {
		this.getPlayerWin = getPlayerWin;
	}
	
	public boolean getPlayerLose() {
		return getPlayerLose;
	}
	public void setPlayerLost(boolean getPlayerLose) {
		this.getPlayerLose = getPlayerLose;
	}

	public boolean getPlayerForfeit() {
		return getPlayerForfeit;
	}
	
	public void setPlayerForfeited(boolean getPlayerForfeit) {
		this.getPlayerForfeit = getPlayerForfeit;
	}


	/**
	 * Set the player for the present round
	 */
	public void setPlayer(Player player) {
		this.player = player;
		player.resetAllPlayerGuesses();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getSecretCode() {
		return secretCode;
	}
	
	public List<String> getGuesses() {
		return guesses;
	}
	
	/**
	 * adding guesses based on win, lose or forfeit
	 */
	public void addGuess(Player player, String guess) {
		// dont to add guesses when win or lose occurs
		if (checkWinner(player) || checkLoser(player)) {
			return;
		}
		
		// add guess if number of current guesses is less than max guesses
		if (player != null && player.getTotalGuesses() < MAX_GUESSES) {
			player.addGuess(guess);
			guesses.add(guess);
		}
		
		// if guess equals secret code then player wins
		if (checkGuessEqual(guess)) {
			setPlayerWon(true);
			return;
		}
		
		// if the 4th present guess is incorrect, player loses
		if (player.getTotalGuesses() >= MAX_GUESSES && checkGuessEqual(guess) == false) {
			setPlayerLost(true);
			return;
		}
	}
	
	public boolean checkWinner(Player player) {
		return this.player == player && getPlayerWin;
	}
	
	
	public boolean checkLoser(Player player) {
		return this.player == player && getPlayerLose;
	}
	
	
	public boolean checkGuessEqual(String guess) {
		return secretCode.equals(guess);
	}
	
	
	public boolean checkForfeit(Player player) {
		return this.player == player && getPlayerForfeit;
	}
	
	public void forfeit(Player player) {
		if (this.player == player) {
			setPlayerForfeited(true);
		}
	}
	
	public boolean checkFinished() {
		return checkFinished;
	}
	
	public void finish() {
		setPlayerLost(true);
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
			
			// check if num exists in secret code
			else if (secretCode.contains(new String(new char[] { guessNum }))) {
				incorrectPos += 1;
			}
		}
		return incorrectPos;
	}
}