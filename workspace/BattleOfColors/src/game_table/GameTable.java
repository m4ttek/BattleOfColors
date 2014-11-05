package game_table;

import game_utils.Colors;

import java.awt.Point;
import java.util.Collection;
import java.util.List;

/**
 * Interfejs dla stołów do gry.
 * 
 * @author Mateusz Kamiński
 */
public interface GameTable {

	public Collection<Colors> getCurrentTable();

	public int getTableWidth();
	
	public int getTableHeight();
	
	public boolean checkIfGameFinished();
	
	public Collection<Point> getPlayersStartingPoints();
	
	public List<Integer> makeHypotheticalMove(int playerId, Colors color);
	
	public void undoHypotheticalMove(int noOfMoves);
	
	public Collection<Colors> getHistoricalTable(int noOfMoves);
}
