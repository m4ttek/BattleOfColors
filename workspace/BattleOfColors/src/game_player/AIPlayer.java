package game_player;

import game_main.GameState;
import game_table.GameTable;
import game_utils.Colors;

public abstract class AIPlayer extends DefaultPlayer {

	protected GameState gameState;
	protected Colors opponentsColor;
	protected Integer opponentsPlayerId;
	
	public AIPlayer(GameTable gameTable, Integer playerPos, GameState gameState,
			Integer opponentsPlayerId) {
		super(gameTable, playerPos);
		this.gameState = gameState;
		this.opponentsPlayerId = opponentsPlayerId;
	}
	
	public abstract void setDifficultyLevel(int level);
}