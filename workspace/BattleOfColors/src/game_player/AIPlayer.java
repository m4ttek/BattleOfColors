package game_player;

import game_main.GameState;
import game_table.GameTable;
import game_utils.Colors;

import java.awt.Point;

public abstract class AIPlayer extends DefaultPlayer {

	protected GameState gameState;
	protected Colors opponentsColor;
	protected Integer opponentsPlayerId;
	
	public AIPlayer(GameTable gameTable, Point playerStartingPosition, GameState gameState,
			Integer opponentsPlayerId) {
		super(gameTable, playerStartingPosition);
		this.gameState = gameState;
		this.opponentsPlayerId = opponentsPlayerId;
	}
	
	public abstract void setDifficultyLevel(int level);
}