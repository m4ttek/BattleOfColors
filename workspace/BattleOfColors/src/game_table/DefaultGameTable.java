package game_table;

import game_utils.Colors;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Klasa reprezentująca podstawową tablicę do gry. 
 * 
 * @author Mateusz Kamiński
 */
public class DefaultGameTable implements GameTable {
	
	private static Integer table_width;
	
	private static Integer table_height;
	
	/**
	 * Domyślny stół gry zawiera początkowe pozycje dla dwóch graczy.
	 */
	private List<Point> playerPositions;
	private Set<Point> fieldsTakenByPlayers;
	
	private static Random RAND = new Random();
	
	List<Colors> tableRepresentation;
	
	List<List<Colors>> historicalTables;
	
	protected List<Set<Point>> historicalTakenFields;
	
	public DefaultGameTable(List<Point> playerPositions, int size) {
		if (playerPositions != null) {
			this.playerPositions = playerPositions;
		}
		table_width=size;
		table_height=size;
		this.playerPositions=Arrays.asList(new Point(table_width / 2, 0), new Point(table_width / 2, table_height - 1));
		tableRepresentation = new ArrayList<Colors>();
		historicalTables = new ArrayList<List<Colors>>();
		historicalTakenFields = new ArrayList<Set<Point>>();
		fieldsTakenByPlayers = new HashSet<Point>();
		fieldsTakenByPlayers.addAll(this.playerPositions);
		generateTable();
	}
	
	@Override
	public Collection<Colors> getCurrentTable() {
		return tableRepresentation;
	}

	@Override
	public int getTableWidth() {
		return table_width;
	}
	
	@Override
	public int getTableHeight() {
		return table_height;
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
		Set<Point> lastFieldsTaken = null;
		if (historicalTables.size() != 0) {
			lastList = historicalTables.get(historicalTables.size() - 1);
			lastFieldsTaken = historicalTakenFields.get(historicalTakenFields.size() - 1);
		} else {
			lastList = tableRepresentation;
			lastFieldsTaken = fieldsTakenByPlayers;
		}
		
		List<Colors> newList = new ArrayList<Colors>(lastList);
		Set<Point> newFieldsTaken = new HashSet<Point>(lastFieldsTaken);
		
		historicalTables.add(newList);
		historicalTakenFields.add(newFieldsTaken);
		
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
		historicalTakenFields = historicalTakenFields.subList(0, historicalTakenFields.size() - noOfMoves);
	}

	@Override
	public List<Colors> getHistoricalTable(int noOfMoves) {
		if (noOfMoves > historicalTables.size()) {
			throw new RuntimeException("getHistoricalTable: request for non-existing historical table");
		}
		return historicalTables.get(historicalTables.size() - noOfMoves - 1);
	}
	
	protected Set<Point> getHistoricalTakenFields(int noOfMoves) {
		return historicalTakenFields.get(historicalTakenFields.size()-1-noOfMoves);
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
		//RAND.setSeed(123);
		for (int y = 0; y < table_height; y++) {
			for (int x = 0; x < table_width; x++) {
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
	
	private ColorGroup getProperGroup(ColorGroup[][] groups, int posY, int posX,
			List<ColorGroup> colorGroups, Set<ColorGroup> firstPlayerGroups,
			Set<ColorGroup> secondPlayerGroups) {
		
		ColorGroup neighborGroup = null;
		
		//check group on the left
		if(posX != 0) {
			neighborGroup = groups[posY][posX-1];
		}
		//check group on the top
		if(posY != 0) {
			if(neighborGroup != null && groups[posY-1][posX] != null &&
					neighborGroup != groups[posY-1][posX]) {

				//join groups
				//System.out.println("Joining " + neighborGroup.getUid() + " and " + groups[posY-1][posX].getUid());
				neighborGroup.join(groups[posY-1][posX], groups, firstPlayerGroups, secondPlayerGroups);
			}
			else if(neighborGroup == null) {
				neighborGroup = groups[posY-1][posX];
			}
		}
		
		if(neighborGroup == null) {
			neighborGroup = new ColorGroup(new Point(posX, posY));
			List<Colors> currentTable = getHistoricalTable(0);
			if(posY>0) {
				//field on the top is player's
				Point playerPosition = playerPositions.get(0);
				if(currentTable.get((posY-1)*table_width+posX) ==
						currentTable.get(playerPosition.y*table_width+playerPosition.x)) {
					firstPlayerGroups.add(neighborGroup);
					//System.out.println("At " + (posY-1) + " " + posX + " we have " +
							//currentTable.get((posY-1)*TABLE_WIDTH+posX));
					//System.out.println("And at " + posY + " " + posX + " we have " +
							//currentTable.get(playerPosition.y*TABLE_WIDTH+playerPosition.x));
					//System.out.println("Added group " + neighborGroup.getUid() + " to player oneY");
				}
				else {
					secondPlayerGroups.add(neighborGroup);
					//System.out.println("Added group " + neighborGroup.getUid() + " to player twoY");
				}
			}
			if(posX>0) {
				//field on the left is player's
				Point playerPosition = playerPositions.get(1);
				if(getHistoricalTable(0).get(posY*table_width+posX-1) ==
						currentTable.get(playerPosition.y*table_width+playerPosition.x)) {
					secondPlayerGroups.add(neighborGroup);
					//System.out.println("Added group " + neighborGroup.getUid() + " to player twoX");
				}
				else {
					firstPlayerGroups.add(neighborGroup);
					//System.out.println("Added group " + neighborGroup.getUid() + " to player oneX");
				}
			}
		}
		else {
			neighborGroup.add(new Point(posX, posY));
		}
		return neighborGroup;
	}
	
	protected boolean isPlayerPos(int playerNo, int y, int x) {
		for(Point p : getHistoricalTakenFields(0)) {
			if(p.y == y && p.x == x) {
				//System.out.println("Field " + p.y + " " + p.x + " is player's");
				Point playerPosition;
				if(playerNo == 0) {
					playerPosition = playerPositions.get(0);
					//System.out.println("first pos is " + playerPosition.y + " " + playerPosition.x);
				}
				else {
					playerPosition = playerPositions.get(1);
					//System.out.println("second pos is " + playerPosition.y + " " + playerPosition.x);
				}
				List<Colors> currentTable = getHistoricalTable(0);
				if(currentTable.get(y*table_width+x) ==
						currentTable.get(playerPosition.y*table_width+playerPosition.x)) {
					//System.out.println("player " + playerNo + playerPosition.x + " " + playerPosition.y);
					//System.out.println(currentTable.get(y*TABLE_WIDTH+x) + " " +
							//currentTable.get(playerPosition.y*TABLE_WIDTH+playerPosition.x));
					////System.out.println("Took " + (//System.nanoTime()-start) + " nanoseconds");
					return true;
				}
			}
		}
		////System.out.println("Took " + (//System.nanoTime()-start) + " nanoseconds");
		return false;
	}
	
	protected List<Integer> findInaccessibleFields(Colors playerColor) {
		//System.out.println("Call no. " + tempCalls++);

		List<Integer> takenFields = new LinkedList<Integer>();
		List<ColorGroup> colorGroups = new LinkedList<ColorGroup>();
		ColorGroup[][] groups = new ColorGroup[table_height][table_width];
		Set<ColorGroup> firstPlayerGroups = new HashSet<ColorGroup>();
		Set<ColorGroup> secondPlayerGroups = new HashSet<ColorGroup>();
		for(int i = 0; i < table_height; i++) {
			for(int j = 0; j < table_width; j++) {
				if(isPlayerPos(0, i, j)) {
					//check group on the left
					if(j > 0 && groups[i][j-1] != null) {
						firstPlayerGroups.add(groups[i][j-1]);
						//System.out.println("Added " + groups[i][j-1].getUid() + "to player one");
					}
					//check group on the top
					if(i > 0 && groups[i-1][j] != null) {
						firstPlayerGroups.add(groups[i-1][j]);
						//System.out.println("Added " + groups[i-1][j].getUid() + "to player one");
					}
				}
				else if(isPlayerPos(1, i, j)) {
					//check group on the left
					if(j > 0 && groups[i][j-1] != null) {
						secondPlayerGroups.add(groups[i][j-1]);
						//System.out.println("Added " + groups[i][j-1].getUid() + "to player two");
					}
					//check group on the top
					if(i > 0 && groups[i-1][j] != null) {
						secondPlayerGroups.add(groups[i-1][j]);
						//System.out.println("Added " + groups[i-1][j].getUid() + "to player two");
					}
				}
				else {
					groups[i][j] = getProperGroup(groups, i, j, colorGroups, firstPlayerGroups,
							secondPlayerGroups);
						//System.out.println("Field " + i + " " + j + " was assigned to group " +
							//groups[i][j].getUid());
				}
			}
		}
		//System.out.println("First player groups: ");
		Set<ColorGroup> currentPlayerGroups;
		Set<ColorGroup> otherPlayerGroups;
		Point playerPosition = playerPositions.get(0);
		if(playerColor == getHistoricalTable(0).get(playerPosition.y*table_width+playerPosition.x)) {
			currentPlayerGroups = firstPlayerGroups;
			otherPlayerGroups = secondPlayerGroups;
		}
		else {
			currentPlayerGroups = secondPlayerGroups;
			otherPlayerGroups = firstPlayerGroups;
		}
		
		for(ColorGroup cg : currentPlayerGroups) {
			//System.out.println(cg.getUid());
			if(otherPlayerGroups.contains(cg)) {
				//System.out.println("Second player also has the group no. " + cg.getUid());
			}
			else {
				//System.out.println("Second player doesn't have group no. " + cg.getUid());
				//add additional fields to player's fields
				for(Point field : cg.getFieldsInGroup()) {
					List<Colors> table = getHistoricalTable(0);
					table.set(field.y*table_width+field.x, playerColor);
					takenFields.add(field.y*table_width+field.x);
					//System.out.println("Field " + field.y + " " + field.x + " set to color " +
							//playerColor);
				}
			}
		}
		ColorGroup.resetCount();
		//System.out.println("End of Call no. " + (tempCalls-1));
		return takenFields;
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
		final Integer originPosition = pos.x + pos.y * table_width;
		final Colors originColor = table.get(originPosition);
		
		if (originPosition < 0 || originPosition >= table.size()) {
			throw new RuntimeException("fillNewColor: bad seed position: " + pos);
		}
		if (originColor == color) {
			throw new RuntimeException("fillNewColor: filling with existing color: " + color);
		}
		
		List<Integer> listOfExploredPositions = new ArrayList<Integer>();
		Set<Integer> listOfNonExploredPositions = new HashSet<Integer>();
		listOfNonExploredPositions.add(originPosition);
		while (listOfNonExploredPositions.size() != 0) {
			Integer exploredPos = listOfNonExploredPositions.iterator().next();
			listOfNonExploredPositions.remove(exploredPos);
			
			// sprawdzenie lewego pola
			if (!(exploredPos % table_width == 0) && !listOfExploredPositions.contains(exploredPos - 1) &&
					((table.get(exploredPos - 1) == originColor && table.get(exploredPos) == originColor || table.get(exploredPos - 1) == color))) {
				listOfNonExploredPositions.add(exploredPos - 1);
			}
			// sprawdzenie prawego pola
			if (!(exploredPos % table_width == table_width - 1) && !listOfExploredPositions.contains(exploredPos + 1) 
					&& (table.get(exploredPos + 1) == originColor && table.get(exploredPos) == originColor || table.get(exploredPos + 1) == color)) {
				listOfNonExploredPositions.add(exploredPos + 1);
			}
			// sprawdzenie dolnego pola
			if (!(exploredPos + table_width >= table.size()) && !listOfExploredPositions.contains(exploredPos + table_width) 
					&& (table.get(exploredPos + table_width) == originColor && table.get(exploredPos) == originColor || table.get(exploredPos + table_width) == color)) {
				listOfNonExploredPositions.add(exploredPos + table_width);
			}
			// sprawdzenie gornego pola
			if (!(exploredPos - table_width < 0) && !listOfExploredPositions.contains(exploredPos - table_width) 
					&& (table.get(exploredPos - table_width) == originColor && table.get(exploredPos) == originColor || table.get(exploredPos - table_width) == color)) {
				listOfNonExploredPositions.add(exploredPos - table_width);
			}
			// zamalowanie pola
			table.set(exploredPos, color);
			if(getHistoricalTable(0).get(exploredPos) != Colors.BLACK) {
				getHistoricalTakenFields(0).add(new Point(exploredPos%table_width, exploredPos/table_width));
			}
			
			listOfExploredPositions.add(exploredPos);
		}
			List<Integer> temp = findInaccessibleFields(color);
			listOfExploredPositions.addAll(temp);
		return listOfExploredPositions;
	}
	

}