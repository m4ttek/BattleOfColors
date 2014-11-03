package game_player;

import java.awt.Point;
import java.util.List;

import game_table.GameTable;
import game_utils.Colors;

public class DefaultPlayer implements Player {

	private final GameTable gameTable;

	private Colors currentPlayerColor;
	
	private List<Integer> takenFields;

	private Point playerStartingPosition;
	
	public DefaultPlayer(GameTable gameTable, Point playerStartingPosition) {
		this.gameTable = gameTable;
		this.playerStartingPosition = playerStartingPosition;
	}
	
	@Override
	public void makeMove() {
		// TODO Auto-generated method stub
		
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
	public Point getPlayerOriginPlace() {
		return playerStartingPosition;
	}


	
}