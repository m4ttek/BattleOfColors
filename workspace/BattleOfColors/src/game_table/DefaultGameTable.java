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

	private static final Integer TABLE_WIDTH = 10;
	
	private static final Integer TABLE_HEIGHT = 10;
	
	/**
	 * Domyślny stół gry zawiera początkowe pozycje dla dwóch graczy.
	 */
	private List<Point> playerPositions = 
			Arrays.asList(new Point(TABLE_WIDTH / 2, 0), new Point(TABLE_WIDTH / 2, TABLE_HEIGHT - 1));
	
	private static Random RAND = new Random();
	
	List<Colors> tableRepresentation;
	
	List<List<Colors>> historicalTables;
	
	public DefaultGameTable(List<Point> playerPositions) {
		if (playerPositions != null) {
			this.playerPositions = playerPositions;
		}
		tableRepresentation = new ArrayList<Colors>();
		historicalTables = new ArrayList<List<Colors>>();
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

	@Override
	public List<Integer> makeHypotheticalMove(Integer playerId, Colors color) {
		List<Colors> lastList = null;
		if (historicalTables.size() != 0) {
			lastList = historicalTables.get(historicalTables.size() - 1);
		} else {
			lastList = tableRepresentation;
		}
		List<Colors> newList = new ArrayList<Colors>(lastList);
		
		historicalTables.add(newList);
		if ( playerId < playerPositions.size()) {
			return fillNewColor(newList, playerPositions.get(playerId), color);
		}
		throw new RuntimeException("makeHypotheticalMove: player with playerId = " + playerId + " does not exist!");
	}

	@Override
	public void undoHypotheticalMove(int noOfMoves) {
		if (noOfMoves > historicalTables.size()) {
			throw new RuntimeException("undoHypothethicalMove: request for non-existing historical table");
		}
		historicalTables = historicalTables.subList(0, historicalTables.size() - noOfMoves);
	}

	@Override
	public Collection<Colors> getHistoricalTable(int noOfMoves) {
		if (noOfMoves > historicalTables.size()) {
			throw new RuntimeException("getHistoricalTable: request for non-existing historical table");
		}
		return historicalTables.get(historicalTables.size() - noOfMoves - 1);
	}
	
	@Override
	public void acceptMove(int noOfMoves) {
		if (noOfMoves > historicalTables.size()) {
			throw new RuntimeException("acceptMove: request for non-existing historical table");
		}
		tableRepresentation = historicalTables.get(historicalTables.size() - noOfMoves - 1);
		historicalTables = new ArrayList<List<Colors>>();
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
	
	/**
	 * Metoda wypełnia nowym kolorem obszar zajety przez wskazany kolor.
	 * 
	 * @param table tablica do wykonania operacji
	 * @param pos początkowa pozycja
	 * @param color nowy kolor wypełnienia
	 * @return lista pól zawierających nowy kolor
	 */
	protected List<Integer> fillNewColor(List<Colors> table, Point pos, Colors color) {
		final Integer originPosition = pos.x + pos.y * TABLE_WIDTH;
		final Colors originColor = table.get(originPosition);
		
		if (originPosition < 0 || originPosition >= table.size()) {
			throw new RuntimeException("fillNewColor: bad seed position: " + pos);
		}
		if (originColor == color) {
			throw new RuntimeException("fillNewColor: filling with existing color: " + color);
		}
		
		List<Integer> listOfExploredPositions = new ArrayList<Integer>();
		List<Integer> listOfNonExploredPositions = new ArrayList<Integer>();
		listOfNonExploredPositions.add(originPosition);
		while (listOfNonExploredPositions.size() != 0) {
			Integer exploredPos = listOfNonExploredPositions.remove(listOfNonExploredPositions.size() - 1);
			
			// zamalowanie pola
			table.set(exploredPos, color);
			
			// sprawdzenie lewego pola
			if (!(exploredPos % TABLE_WIDTH == 0) && !listOfExploredPositions.contains(exploredPos - 1) &&
					(table.get(exploredPos - 1) == originColor || table.get(exploredPos - 1) == color)) {
				listOfNonExploredPositions.add(exploredPos - 1);
			}
			// sprawdzenie prawego pola
			if (!(exploredPos % TABLE_WIDTH == TABLE_WIDTH - 1) && !listOfExploredPositions.contains(exploredPos + 1) 
					&& (table.get(exploredPos + 1) == originColor || table.get(exploredPos + 1) == color)) {
				listOfNonExploredPositions.add(exploredPos + 1);
			}
			// sprawdzenie dolnego pola
			if (!(exploredPos + TABLE_WIDTH >= table.size()) && !listOfExploredPositions.contains(exploredPos + TABLE_WIDTH) && (table.get(exploredPos + TABLE_WIDTH) == originColor || table.get(exploredPos + TABLE_WIDTH) == color)) {
				listOfNonExploredPositions.add(exploredPos + TABLE_WIDTH);
			}
			// sprawdzenie gornego pola
			if (!(exploredPos - TABLE_WIDTH < 0) && !listOfExploredPositions.contains(exploredPos - TABLE_WIDTH) && (table.get(exploredPos - TABLE_WIDTH) == originColor || table.get(exploredPos - TABLE_WIDTH) == color)) {
				listOfNonExploredPositions.add(exploredPos - TABLE_WIDTH);
			}
			listOfExploredPositions.add(exploredPos);
		}
		return listOfExploredPositions;
	}

}