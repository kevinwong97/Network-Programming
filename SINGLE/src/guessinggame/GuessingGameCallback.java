package guessinggame;

/**
 * events that are called by main classes
 * 
 * @author Kevin W
 */
public interface GuessingGameCallback {
	
	/**
	 * initiate game
	 */
	void gcbInitiate(GuessingGame game);
	
	/**
	 * generated secret code
	 */
	void gcbGeneratedSecretCode(GuessingGame game, String secretCode);

	/**
	 * round begin
	 */
	void gcbRoundBegin(GuessingGame game, GuessingGameRound round, Player player);

	/**
	 * player registered
	 */
	void gcbPlayerRegistered(GuessingGame game, GuessingGameRound round, Player player);

	/**
	 * add guess num
	 */
	void gcbAddGuessNum(GuessingGameRound round, Player player, String guess);

	/**
	 * incorrect guess
	 */
	void gcbIncorrectGuess(GuessingGameRound round, Player player, String guess);

	/**
	 * player win
	 */
	void gcbPlayerWin(GuessingGameRound round, Player player, int numOfGuesses);

	/**
	 * palyer lost
	 */
	void gcbPlayerLose(GuessingGameRound round, Player player, String secretCode);

	/**
	 * round finish
	 */
	void gcbRoundFinish(GuessingGame game, GuessingGameRound round);

	/**
	 * player forfeit
	 */
	void gcbPlayerForfeit(GuessingGame game, GuessingGameRound round, Player player);

	/**
	 * player quit
	 */
	void gcbPlayerQuit(GuessingGame game, Player player);
	

}
