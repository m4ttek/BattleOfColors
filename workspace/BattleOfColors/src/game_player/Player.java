package game_player;

import java.awt.Point;

import game_utils.Colors;

public interface Player {
	
	public void makeMove();
	
	public void setChosenColor(Colors chosenColor);
	
	public Colors getPlayerColor();
	
	public Integer getNumberOfFieldsTakenByPlayer();
	
	public Point getPlayerOriginPlace();
}