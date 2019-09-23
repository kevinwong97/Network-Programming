package guessinggame;

import java.util.ArrayList;
import java.util.List;
import java.util.*;


/**
 * this class holds most of functionality, This game mode has multiple gameRounds with up to 6 players. 
 * @author Kevin W
 *
 */
public class GuessingGame {
	
	public static final int MAX_PLAYERS = 6;
	public static final int MIN_PLAYERS = 3;
	public static final int MIN_NUMS = 0;
	public static final int MAX_NUMS = 9;
	
	/**
	 * multiple game rounds
	 */
	List<GuessingGameRound> gameRounds = new ArrayList<>();
	private GuessingGameRound presentRound;
	
	/**
	 * multiple players peround
	 */
	private List<Player> players = new ArrayList<>();
	private int numD = 0;
	
	public GuessingGame() {}
	
	public void start() {}
	
	
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
	public void removePlayer(Player player) {
		players.remove(player);
	}
	
	
	/**
	 * Finish the present round and start the new round. 
	 * A new random number is generated from 0-9 
	 */
	public GuessingGameRound beginTheNextRound() throws Exception {
		if (presentRound != null && !presentRound.checkFinished() ) {
			throw new Exception("The present round has not finished");
		}
		
		if (players.size() == 0) {
			throw new Exception("There are no players at the moment");
		}

		String secretCode = generateSecretCode(numD);
		presentRound = new GuessingGameRound(secretCode);
		// add players
		for (Player player : players) {
			player.resetGuesses();
			presentRound.addPlayer(player);
		}
		gameRounds.add(presentRound);
		
		
		return getPresentRound();
	}
	
	
	/**
	 * Sets the player as the current player in that game and creates a new instance 
	 */
	public synchronized Player registerPlayer(String playerName) throws Exception {
		if (getPlayers().size() == MAX_PLAYERS) {
			throw new Exception("Error cant add more than " + MAX_PLAYERS + " players");
		}
		
		Player player = new Player(playerName);
		players.add(player);
		
		return player;
	}
	
	// return players in current round
	public List<Player> getPlayers() {
		return players;
	}
	
}









