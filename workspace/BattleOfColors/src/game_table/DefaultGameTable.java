package game_table;

import game_utils.Colors;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
	private List<Integer> playerPositions;
	private Set<Integer> fieldsTakenByPlayers;
	
	private static Random RAND = new Random();
	
	Map<Integer, Colors> tableRepresentation;
	
	Map<Integer, Map<Integer, Colors>> historicalTables;
	
	protected Map<Integer, Set<Integer>> historicalTakenFields;
	
	public DefaultGameTable(List<Integer> playerPositions, int size) {
		if (playerPositions != null) {
			this.playerPositions = playerPositions;
		}
		table_width=size;
		table_height=size;
		this.playerPositions=Arrays.asList(table_width / 2, table_width * (table_height - 1) + table_width / 2);
		tableRepresentation = new HashMap<Integer, Colors>();
		historicalTables = new HashMap<Integer, Map<Integer, Colors>>();
		historicalTakenFields = new HashMap<Integer, Set<Integer>>();
		fieldsTakenByPlayers = new HashSet<Integer>(10000);
		fieldsTakenByPlayers.addAll(this.playerPositions);
		generateTable();
	}
	
	@Override
	public Map<Integer, Colors> getCurrentTable() {
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
	
	public List<Integer> getPlayersStartingPoints() {
		return playerPositions;
	}

	@Override
	public Set<Integer> makeHypotheticalMove(Integer playerId, Colors color) {
		Map<Integer, Colors> lastList = null;
		Set<Integer> lastFieldsTaken = null;
		int historicalTablesSize = historicalTables.size();
		int historicalTakenFieldsSize = historicalTakenFields.size();
		if (historicalTablesSize != 0) {
			lastList = historicalTables.get(historicalTablesSize - 1);
			lastFieldsTaken = historicalTakenFields.get(historicalTakenFieldsSize - 1);
		} else {
			lastList = tableRepresentation;
			lastFieldsTaken = fieldsTakenByPlayers;
		}
		Map<Integer, Colors> newList = new HashMap<Integer, Colors>(lastList);
		Set<Integer> newFieldsTaken = new HashSet<Integer>(lastFieldsTaken);
		
		historicalTables.put(historicalTablesSize, newList);
		historicalTakenFields.put(historicalTakenFieldsSize, newFieldsTaken);
		
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
		for (int i = 0; i < noOfMoves; i++) {
			historicalTakenFields.remove(historicalTakenFields.size() - 1);
			historicalTables.remove(historicalTables.size() - 1);
		}
	}

	@Override
	public Map<Integer, Colors> getHistoricalTable(int noOfMoves) {
		if (noOfMoves > historicalTables.size()) {
			throw new RuntimeException("getHistoricalTable: request for non-existing historical table");
		}
		return historicalTables.get(historicalTables.size() - noOfMoves - 1);
	}
	
	protected Set<Integer> getHistoricalTakenFields(int noOfMoves) {
		return historicalTakenFields.get(historicalTakenFields.size()-1-noOfMoves);
	}
	
	@Override
	public void acceptMove(int noOfMoves) {
		if (noOfMoves > historicalTables.size()) {
			throw new RuntimeException("acceptMove: request for non-existing historical table");
		}
		tableRepresentation = historicalTables.get(historicalTables.size() - noOfMoves - 1);
		historicalTables = new HashMap<Integer, Map<Integer, Colors>>();
	}
	
	private void generateTable() {
		Iterator<Integer> pointIterator = playerPositions.iterator();
		Integer pos = pointIterator.next();
		//RAND.setSeed(123);
		for (int i = 0; i < table_height * table_width; i++) {
			// dodajemy poczatkowe pozycje graczy
			if ( pos != null && i == pos) {
				tableRepresentation.put(tableRepresentation.size(), Colors.BLACK);
				if (pointIterator.hasNext()) {
					pos = pointIterator.next();
				} else {
					pos = null;
				}
			} else {
				tableRepresentation.put(tableRepresentation.size(), Colors.values()[RAND.nextInt(Colors.values().length - 1) + 1]);
			}
		}
	}
	
	private ColorGroup getProperGroup(ColorGroup[][] groups, int posY, int posX,
			 Set<ColorGroup> firstPlayerGroups,
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
			Map<Integer, Colors> currentTable = getHistoricalTable(0);
			if(posY>0) {
				//field on the top is player's
				Integer playerPosition = playerPositions.get(0);
				if(currentTable.get((posY-1)*table_width+posX) ==
						currentTable.get(playerPosition)) {
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
				Integer playerPosition = playerPositions.get(1);
				if(getHistoricalTable(0).get(posY*table_width+posX-1) ==
						currentTable.get(playerPosition)) {
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
	
	protected boolean isPlayerPos(int playerNo, Integer pos) {
		if(getHistoricalTakenFields(0).contains(pos)) {
			//System.out.println("Field " + p.y + " " + p.x + " is player's");
			Integer playerPosition = playerPositions.get(playerNo);
			Map<Integer, Colors> currentTable = getHistoricalTable(0);
			if(currentTable.get(pos) ==
					currentTable.get(playerPosition)) {
				//System.out.println("player " + playerNo + playerPosition.x + " " + playerPosition.y);
				//System.out.println(currentTable.get(y*TABLE_WIDTH+x) + " " +
						//currentTable.get(playerPosition.y*TABLE_WIDTH+playerPosition.x));
				////System.out.println("Took " + (//System.nanoTime()-start) + " nanoseconds");
				return true;
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
				if(isPlayerPos(0, i* table_width + j)) {
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
				else if(isPlayerPos(1, i* table_width + j)) {
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
					groups[i][j] = getProperGroup(groups, i, j, firstPlayerGroups,
							secondPlayerGroups);
						//System.out.println("Field " + i + " " + j + " was assigned to group " +
							//groups[i][j].getUid());
				}
			}
		}
		//System.out.println("First player groups: ");
		Set<ColorGroup> currentPlayerGroups;
		Set<ColorGroup> otherPlayerGroups;
		Integer playerPosition = playerPositions.get(0);
		if(playerColor == getHistoricalTable(0).get(playerPosition)) {
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
					Map<Integer, Colors> table = getHistoricalTable(0);
					table.put(field.y*table_width+field.x, playerColor);
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
	 * @param tableMap tablica do wykonania operacji
	 * @param pos początkowa pozycja
	 * @param color nowy kolor wypełnienia
	 * @return lista pól zawierających nowy kolor
	 */
	protected Set<Integer> fillNewColor(Map<Integer, Colors> tableMap, Integer originPosition, Colors color) {
		final Colors originColor = tableMap.get(originPosition);
		
		if (originPosition < 0 || originPosition >= tableMap.size()) {
			throw new RuntimeException("fillNewColor: bad seed position: " + originPosition);
		}
		if (originColor == color) {
			throw new RuntimeException("fillNewColor: filling with existing color: " + color);
		}

		Set<Integer> setOfExploredPositions = new HashSet<Integer>();
		Set<Integer> setOfMainPositions =  new HashSet<Integer>();
		setOfMainPositions.add(originPosition);
		
		while (!setOfMainPositions.isEmpty()) {
			Integer mainPosition = setOfMainPositions.iterator().next();
			
			// poruszamy się maksymalnie w lewo, pózniej w prawo od mainPoisition
			boolean isLeftDir = true;
			Integer pointToCheck = mainPosition;
			boolean onNewFields = false;
			boolean onAlsoRightNewFields = false;
			if (tableMap.get(mainPosition) == color) {
				onNewFields = true;
				onAlsoRightNewFields = true;
			}
			while (true) {
				if (!onNewFields && (tableMap.get(pointToCheck) == originColor || tableMap.get(pointToCheck) == color)) {
					if (tableMap.get(pointToCheck) == originColor) {
						tableMap.put(pointToCheck, color);
					} else {
						onNewFields = true;
					}
				} else if (onNewFields && tableMap.get(pointToCheck) == color) {
				} else {
					if (!isLeftDir) {
						break;
					}
					pointToCheck = mainPosition + 1;
					if (pointToCheck % table_width == 0) {
						break;
					}
					isLeftDir = false;
					onNewFields = onAlsoRightNewFields;
					continue;
				}
				if (!(pointToCheck - table_width < 0) && !setOfExploredPositions.contains(pointToCheck - table_width) 
						&& (!onNewFields && tableMap.get(pointToCheck - table_width) == originColor || (tableMap.get(pointToCheck - table_width) == color))) {
					setOfMainPositions.add(pointToCheck - table_width);
				}
				if (!(pointToCheck + table_width > table_height * table_width) && !setOfExploredPositions.contains(pointToCheck + table_width) 
						&& (!onNewFields && tableMap.get(pointToCheck + table_width) == originColor || (tableMap.get(pointToCheck + table_width) == color))) {
					setOfMainPositions.add(pointToCheck + table_width);
				}
				setOfExploredPositions.add(pointToCheck);
				setOfMainPositions.remove(pointToCheck);
				
				addPointToHistoricalTable(pointToCheck);
				
				if (isLeftDir) {
					pointToCheck = pointToCheck - 1;
					if (pointToCheck % table_width == table_width - 1) {
						isLeftDir = false;
						pointToCheck = mainPosition + 1;
						onNewFields = onAlsoRightNewFields;
					}
				} else {
					pointToCheck = pointToCheck + 1;
					if (pointToCheck % table_width == 0) {
						break;
					}
				}
			}
		}
		setOfExploredPositions.addAll(findInaccessibleFields(color));
		return setOfExploredPositions;
	}
	
	private void addPointToHistoricalTable(int point) {
		if(getHistoricalTable(0).get(point) != Colors.BLACK) {
			getHistoricalTakenFields(0).add(point);
		}
	}
	
	@Deprecated
	protected Set<Integer> oldFillNewColor(Map<Integer, Colors> tableMap, Integer originPosition, Colors color) {
		final Colors originColor = tableMap.get(originPosition);
		
		if (originPosition < 0 || originPosition >= tableMap.size()) {
			throw new RuntimeException("fillNewColor: bad seed position: " + originPosition);
		}
		if (originColor == color) {
			throw new RuntimeException("fillNewColor: filling with existing color: " + color);
		}
		Set<Integer> listOfExploredPositions = new HashSet<Integer>();
		Set<Integer> listOfNonExploredPositions = new HashSet<Integer>();
		listOfNonExploredPositions.add(originPosition);
		while (listOfNonExploredPositions.size() != 0) {
			Integer exploredPos = listOfNonExploredPositions.iterator().next();
			listOfNonExploredPositions.remove(exploredPos);
			
			// sprawdzenie lewego pola
			if (!(exploredPos % table_width == 0) && !listOfExploredPositions.contains(exploredPos - 1) &&
					((tableMap.get(exploredPos - 1) == originColor && tableMap.get(exploredPos) == originColor || tableMap.get(exploredPos - 1) == color))) {
				listOfNonExploredPositions.add(exploredPos - 1);
			}
			// sprawdzenie prawego pola
			if (!(exploredPos % table_width == table_width - 1) && !listOfExploredPositions.contains(exploredPos + 1) 
					&& (tableMap.get(exploredPos + 1) == originColor && tableMap.get(exploredPos) == originColor || tableMap.get(exploredPos + 1) == color)) {
				listOfNonExploredPositions.add(exploredPos + 1);
			}
			// sprawdzenie dolnego pola
			if (!(exploredPos + table_width >= tableMap.size()) && !listOfExploredPositions.contains(exploredPos + table_width) 
					&& (tableMap.get(exploredPos + table_width) == originColor && tableMap.get(exploredPos) == originColor || tableMap.get(exploredPos + table_width) == color)) {
				listOfNonExploredPositions.add(exploredPos + table_width);
			}
			// sprawdzenie gornego pola
			if (!(exploredPos - table_width < 0) && !listOfExploredPositions.contains(exploredPos - table_width) 
					&& (tableMap.get(exploredPos - table_width) == originColor && tableMap.get(exploredPos) == originColor || tableMap.get(exploredPos - table_width) == color)) {
				listOfNonExploredPositions.add(exploredPos - table_width);
			}
			// zamalowanie pola
			tableMap.put(exploredPos, color);
			if(getHistoricalTable(0).get(exploredPos) != Colors.BLACK) {
				getHistoricalTakenFields(0).add(exploredPos);
			}
			
			listOfExploredPositions.add(exploredPos);
		}
		listOfExploredPositions.addAll(findInaccessibleFields(color));
		return listOfNonExploredPositions;
	}
}