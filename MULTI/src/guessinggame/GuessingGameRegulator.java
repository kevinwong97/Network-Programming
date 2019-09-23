package guessinggame;

import java.util.ArrayList;
import java.util.List;

import guessinggame.Player.PlayerStatus;

/**
 * Each connection to the client will create a new instance of the game regulator.
 * @author Kevin W
 */
public class GuessingGameRegulator {
	
	private GuessingGame game;
	private List<GuessingGameCallback> callbacks = new ArrayList<>();
	private Player presentPlayer;
	
	public GuessingGameRegulator(GuessingGame game) {
		this.game = game;
	}

	public GuessingGame getGame() {
		return game;
	}
	
	public void setGame(GuessingGame game) {
		this.game = game;
	}

	public Player getPresentPlayer() {
		return presentPlayer;
	}
	
	public void setPresentPlayer(Player player) {
		this.presentPlayer = player;
	}
	
	public void addCB(GuessingGameCallback callb) {
		callbacks.add(callb);
	}
	/**
	* get first player in the game
	*/
	public Player getFirstPlayer() {
		return game.getPlayers().get(0);
	}
	
	
	/**
	 * check if present player is actually first
	 */
	public boolean checkPresentPlayerFirst() {
		return getFirstPlayer() == presentPlayer;
	}
	
	/**
	 * Gets number of players in the game
	 * @return
	 */
	public int getNumPlayers() {
		return game.getPlayers().size();
	}
	
	
	/**
	 * activates gcbPlayerRegistered() and set/register the present name 
	 */
	public void setPresentPN(String playerName) throws Exception {
		Player player = game.registerPlayer(playerName);
		setPresentPlayer(player);
		callbacks.forEach(call -> call.gcbPlayerRegistered(game, null, player));
	}

	/**
	 * return true if player forfeit the present round
	 */
	public boolean checkPresentPlayerForfeit() {
		return presentPlayer.checkForfeit(game.getPresentRound());
	}
	
	/**
	 * new round begin if no rounds at the moment
	 * generate a secret code and round begin
	 */
	public void beginTheNextRound() throws Exception {
		GuessingGameRound round = game.getPresentRound();
		
		// first round
		if (round == null) {
			round = game.beginTheNextRound();
			final String secretCode = round.getSecretCode();
			callbacks.forEach(call -> call.gcbGeneratedSecretCode(game, secretCode));
		}
		
		// previous round has ended
		else if (round.checkFinished()) {
			round = game.beginTheNextRound();
			final String nextSecretCode = round.getSecretCode();
			callbacks.forEach(call -> call.gcbGeneratedSecretCode(game, nextSecretCode));
		}
		
		game.getPlayers().forEach(p -> p.setStatus(PlayerStatus.STARTED));
		
		for (GuessingGameCallback call : callbacks) {
			call.gcbRoundBegin(game, round, presentPlayer);
		}
	}
	
	
    /**
	 * Add player guess to present round
	 * If player added a number guess gcbAddGuessNum() is called.
	 * if player forfeits by typing f, the onForfeit() is called 
	 * If player guess is incorrect, the gcbIncorrectGuess() is called, 
	 * If player guess is correct then they win and the gcbPlayerWin() is called.
	 * If player guess incorrectly on the 4th guess, the player loses and the gcbPlayerLose() is called
	 * Check if round has finished base on player win, lose, forfeit 
	 */
	public void addGuess(String guess) {
		GuessingGameRound round = game.getPresentRound();
		
		// player forfeits
		if (guess.trim().equals("f")) {
			
			round.forfeit(presentPlayer);
			presentPlayer.setStatus(PlayerStatus.FORFEITED);
			callbacks.forEach(call -> call.gcbPlayerForfeit(game, round, presentPlayer));
			checkRoundFinished(round);
			return;
		}
		
		// add player guess
		presentPlayer.setStatus(PlayerStatus.PLAYING);
		round.addGuess(presentPlayer, guess);
		callbacks.forEach(call -> call.gcbAddGuessNum(round, presentPlayer, guess));
		
		// if guess is incorrect from the secret code
		if (! round.checkGuessEqual(guess)) {
			callbacks.forEach(call -> call.gcbIncorrectGuess(round, presentPlayer, guess));
		}

		// if player wins meaning they were correct
		if (checkPlayerWin()) {
			presentPlayer.setStatus(PlayerStatus.WON);
			callbacks.forEach(call -> call.gcbPlayerWin(round, presentPlayer, presentPlayer.getTotalGuesses()));
		}

		// by the 4th guess the player has lost
		if (checkPlayerLose()){
			presentPlayer.setStatus(PlayerStatus.LOST);
			callbacks.forEach(call -> call.gcbPlayerLose(round, presentPlayer, round.getSecretCode()));
		}
		
		checkRoundFinished(round);
	}
	
	/**
	 * check if round finished
	 */
	private void checkRoundFinished(GuessingGameRound round) {
		boolean ended = true;
		for (Player player : round.getPlayers()) {
			if (player.checkWin(round) || player.checkLose(round)) {
				continue;
			}
			
			if (player.getTotalGuesses() < GuessingGameRound.MAX_GUESSES) {
				ended = false;
				break;
			}
		}
		
		if (ended) {
			round.finish();
		}
	}
	
	
	/**
	 * make the round finish
	 */
	public void finishCurrentRound() {
		GuessingGameRound round = game.getPresentRound();
		if (! round.checkFinished()) {
			round.finish();
		}
		
		for (GuessingGameCallback call : callbacks) {
			call.gcbRoundFinish(game, round);
		}
	}
	
	
	/**
	 * check present round if player win
	 */
	public boolean checkPlayerWin() {
		return presentPlayer.checkWin(game.getPresentRound());
	}
	
	/**
	 * check present round if player lose
	 */
	public boolean checkPlayerLose() {
		return presentPlayer.checkLose(game.getPresentRound());
	}
	
	public void removePresentPlayer() {
		game.removePlayer(presentPlayer);
	}
	
	/**
	 * player selects quit
	 */
	public void playerQuited() {
		presentPlayer.setStatus(PlayerStatus.QUITED);
		callbacks.forEach(call -> call.gcbPlayerQuit(game, presentPlayer));
		removePresentPlayer();
	}
	
	/**
	 * Check if present game round finished
	 */
	public boolean checkPresentRoundFinished() {
		return game.getPresentRound().checkFinished();
	}
	
	/**
     * check if player entered quit (q) or continue (p) 
	 */
	public boolean isAllOtherPlayersChosenToContinueOrQuit() {
		for (Player player : game.getPlayers()) {
			if (this.presentPlayer == player) 
				continue; 
			
			switch (player.getStatus()) {
				case NOT_STARTED:
				case STARTED:
				case FORFEITED:
				case LOST:
				case PLAYING:
				case WON:
					return false;
				case QUITED:
				case CHOSEN_TO_CONTINUE:
				default: 
					break;
			}
			
			continue;
		}
		return true;
	}
	
	
	
   /**
	* check if player status is continue
	*/
	public void checkContinue() {
		presentPlayer.setStatus(PlayerStatus.CHOSEN_TO_CONTINUE);
	}
	
	/**
	 *  check if all other players in the round quitted
	 */
	public boolean isAllOtherPlayersInRoundQuited(GuessingGameRound round) {
		if (round.getPlayers().size() == 0) {
			return true;
		}
		
		for (Player player : round.getPlayers()) {
			if (player.getStatus() != PlayerStatus.QUITED) {
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * adds present player to present round
	 */
	public void joinPresentPlayer() {
		if (presentPlayer == null) {
			return;
		}
		GuessingGameRound round = game.getPresentRound();
		if (! round.getPlayers().contains(presentPlayer)) {
			presentPlayer.resetGuesses();
			game.getPresentRound().addPlayer(presentPlayer);
		}
		
	}
}
