package game_player;

import java.awt.Point;

import game_table.GameTable;
import game_utils.Colors;

public class AIPlayer implements Player {

	private final GameTable gameTable;
	private Point playerStartingPosition;

	public AIPlayer(GameTable gameTable, Point playerStartingPosition) {
		this.gameTable = gameTable;
		this.playerStartingPosition = playerStartingPosition;
	}
	
	@Override
	public void makeMove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChosenColor(Colors chosenColor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Colors getPlayerColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getNumberOfFieldsTakenByPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getPlayerOriginPlace() {
		// TODO Auto-generated method stub
		return null;
	}
}