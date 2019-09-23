package guessinggame;


import java.util.*;

/**
 * this class holds most of functionality, This game mode has multiple gameRounds but only 1 player. 
 * @author Kevin W
 *
 */
public class GuessingGame {
	
	public static final int MAX_PLAYERS = 6;
	public static final int MIN_PLAYERS = 3;
	public static final int MIN_NUMS = 0;
	public static final int MAX_NUMS = 9;
	
	List<GuessingGameRound> gameRounds = new ArrayList<>();
	private GuessingGameRound presentRound;
	
	private Player player;
	private int numD = 0;
	
	public GuessingGame() {
		//
	}
	
	/**
	 * method that generates a new secret code.
	 * A secret code can be a number from 0-9 
	 * 
	 */
	public String generateSecretCode(int numD) {
        Random rand = new Random();
        int digit = rand.nextInt((9 - 0) + 1) + 0;
        String digitString = Integer.toString(digit);
        
     return digitString.toString();

	}

	
    /**
	 * Get the present round 
	 */
	public GuessingGameRound getPresentRound() {
		return presentRound;
	}
	
	/**
	 * The targeted player is set to null
	 */
	public void removePlayer() {
		player = null;
	}
	
	
	/**
	 * Finish the present round and initiate the new round. 
	 * A new random number is generated from 0-9 
	 */
	public GuessingGameRound beginTheNextRound() throws Exception {
		if (presentRound != null && !presentRound.checkFinished() ) {
			throw new Exception("The present round has not finished");
		}
		
		if (player == null) {
			throw new Exception("Player has not been set");
		}
		
	
		String secretCode = generateSecretCode(numD);
		presentRound = new GuessingGameRound(secretCode);
		player.resetGuesses();
		presentRound.setPlayer(player);
		gameRounds.add(presentRound);
		
		return getPresentRound();
	}
	
	/**
	 * Sets the player as the current player in that game and creates a new instance 
	 */
	public Player registerPlayer(String playerName) throws Exception {
		if (player != null) {
			throw new Exception("Player has already registered");
		}
		
		player = new Player(playerName);
		return player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}