package game_table;

import game_utils.Colors;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Klasa reprezentująca podstawową tablicę do gry. 
 * 
 * @author Mateusz Kamiński
 */
public class DefaultGameTable implements GameTable {

	private static final Integer TABLE_WIDTH = 20;
	
	private static final Integer TABLE_HEIGHT = 20;
	
	/**
	 * Domyślny stół gry zawiera początkowe pozycje dla dwóch graczy.
	 */
	private List<Point> playerPositions = 
			Arrays.asList(new Point(TABLE_WIDTH / 2, 0), new Point(TABLE_WIDTH / 2, TABLE_HEIGHT - 1));
	
	private static Random RAND = new Random();
	
	List<Colors> tableRepresentation;
	
	public DefaultGameTable(List<Point> playerPositions) {
		if (playerPositions != null) {
			this.playerPositions = playerPositions;
		}
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
	
	public List<Point> getPlayersStartingPoints() {
		return playerPositions;
	}

	private void generateTable() {
		Iterator<Point> pointIterator = playerPositions.iterator();
		Point pos = pointIterator.next();
		for (int y = 0; y < TABLE_HEIGHT; y++) {
			for (int x = 0; x < TABLE_WIDTH; x++) {
				// dodajemy poczatkowe pozycje graczy
				if ( pos != null && x == pos.x && y == pos.y) {
					tableRepresentation.add(Colors.BLACK);
					if (pointIterator.hasNext()) {
						pos = pointIterator.next();
					} else {
						pos = null;
					}
				} else {
					tableRepresentation.add(Colors.values()[RAND.nextInt(Colors.values().length - 1) + 1]);
				}
			}
		}
	}

}