package game_player;

import game_table.GameTable;
import game_utils.Colors;

import java.awt.Color;
import java.util.Collection;

public class DefaultPlayer implements Player {

	private final GameTable gameTable;

	public DefaultPlayer(GameTable gameTable) {
		this.gameTable = gameTable;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void makeMove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setChosenColor(Color chosenColor) {
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
	public Integer getPlayerOriginPlace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Colors> getAvailableColorsForPlayer() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}