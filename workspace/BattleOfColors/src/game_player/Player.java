package game_player;

import java.awt.Color;
import java.util.Collection;

import game_utils.Colors;

public interface Player {
	
	public void makeMove();
	
	public void setChosenColor(Color chosenColor);
	
	public Colors getPlayerColor();
	
	public Integer getNumberOfFieldsTakenByPlayer();
	
	public Integer getPlayerOriginPlace();
}