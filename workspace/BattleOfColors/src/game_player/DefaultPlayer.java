package game_player;

import game_table.GameTable;
import game_utils.Colors;

import java.util.HashSet;
import java.util.Set;

/**
 * Klasa reprezentująca podstawowego, domyślnego gracza - człowieka.
 * 
 * @author Mateusz Kamiński
 */
public class DefaultPlayer implements Player {

	static int PLAYER_COUNT = 0;
	
	protected final GameTable gameTable;

	protected Colors currentPlayerColor;
	
	protected Set<Integer> takenFields;

	private Integer playerStartingPosition;
	
	protected Integer playerId;
	
	public DefaultPlayer(GameTable gameTable, Integer playerPos) {
		this.gameTable = gameTable;
		this.playerStartingPosition = playerPos;
		takenFields = new HashSet<Integer>(10000);
		playerId = PLAYER_COUNT;
		PLAYER_COUNT++;
	}
	static public void reset(){
		PLAYER_COUNT = 0;
	}
	@Override
	public void makeMove() {
		takenFields = gameTable.makeHypotheticalMove(playerId, currentPlayerColor);
		gameTable.acceptMove(0);
	}

	@Override
	public void setChosenColor(Colors chosenColor) {
		currentPlayerColor = chosenColor;
	}

	@Override
	public Colors getPlayerColor() {
		return currentPlayerColor;
	}

	@Override
	public Integer getNumberOfFieldsTakenByPlayer() {
		return takenFields.size();
	}

	@Override
	public Integer getPlayerOriginPlace() {
		return playerStartingPosition;
	}
	
	public Integer getPlayerId() {
		return playerId;
	}
}