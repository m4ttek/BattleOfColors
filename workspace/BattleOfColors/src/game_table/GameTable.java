package game_table;

import game_utils.Colors;

import java.util.Collection;

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
}
