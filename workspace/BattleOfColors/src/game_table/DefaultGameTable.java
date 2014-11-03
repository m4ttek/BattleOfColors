package game_table;

import game_utils.Colors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Klasa reprezentująca podstawową tablicę do gry. 
 * 
 * @author Mateusz Kamiński
 */
public class DefaultGameTable implements GameTable {

	private static Integer TABLE_WIDTH = 20;
	
	private static Integer TABLE_HEIGHT = 20;
	
	private static Random RAND = new Random();
	
	List<Colors> tableRepresentation;
	
	public DefaultGameTable() {
		tableRepresentation = new ArrayList<Colors>();
		generateTable();
	}
	
	@Override
	public Collection<Colors> getCurrentTable() {
		return tableRepresentation;
	}

	@Override
	public int getTableWidth() {
		return TABLE_WIDTH;
	}
	
	@Override
	public int getTableHeight() {
		return TABLE_HEIGHT;
	}
	
	@Override
	public boolean checkIfGameFinished() {
		return false;
	}

	private void generateTable() {
		for (int y = 0; y < TABLE_HEIGHT; y++) {
			for (int x = 0; x < TABLE_WIDTH; x++) {
				// poczatkowe pozycje graczy
				if ( x == TABLE_WIDTH / 2 && (y == 0 || y == TABLE_HEIGHT - 1)) {
					tableRepresentation.add(Colors.BLACK);
				} else {
					tableRepresentation.add(Colors.values()[RAND.nextInt(Colors.values().length - 1) + 1]);
				}
			}
		}
	}

}