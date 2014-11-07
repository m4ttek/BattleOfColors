package game_main;

import game_player.AIPlayer;
import game_player.DefaultPlayer;
import game_player.Player;
import game_table.DefaultGameTable;
import game_table.GameTable;
import game_utils.Colors;
import game_utils.PlayerType;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Klasa zawierająca stan gry i możliwe operacje.
 * 
 * @author Mateusz Kamiński
 */
public class GameState {
	
	private static GameState gameState;

	private List<Player> listOfPlayers;
	
	private GameTable gameTable;
	
	private Player currentPlayer;
	
	private int playerIdx;
	
	private GameState(List<PlayerType> players) {
		gameTable = new DefaultGameTable(null);
		listOfPlayers = new ArrayList<Player>();
		
		Collection<Point> playersStartingPoints = gameTable.getPlayersStartingPoints();
		Iterator<Point> iterator = playersStartingPoints.iterator();
		
		for (PlayerType playerType : players) {
			Point playerPos = iterator.next();
			if (playerType.equals(PlayerType.AI)) {
				listOfPlayers.add(new AIPlayer(gameTable, playerPos));
			} else if (playerType.equals(PlayerType.HUMAN)) {
				listOfPlayers.add(new DefaultPlayer(gameTable, playerPos));
			}
		}
		currentPlayer = listOfPlayers.get(playerIdx++);
	}
	
	public static GameState startGame(List<PlayerType> playerTypeList) {
		if (gameState == null) {
			gameState = new GameState(playerTypeList);
		}
		return gameState;
	}
	
	public static GameState restartGame(List<PlayerType> playerTypeList) {
		gameState = null;
		return GameState.startGame(playerTypeList);
	}
	
	public void makeNextMove(String moveParameters) throws IncorrectColorException {
		Colors chosenColor = Colors.valueOf(moveParameters);
		if (!getAvailableColorsForCurrentPlayer().contains(chosenColor)) {
			throw new IncorrectColorException(currentPlayer.getPlayerId(), chosenColor);
		}
		currentPlayer.setChosenColor(chosenColor);
		currentPlayer.makeMove();
		currentPlayer = listOfPlayers.get(playerIdx++);
		if (playerIdx % listOfPlayers.size() == 0) {
			playerIdx = 0;
		}
	}
	
	public boolean isGameFinished() {
		int fieldSum = 0;
		for (Player player : listOfPlayers) {
			fieldSum += player.getNumberOfFieldsTakenByPlayer();
		}
		if (fieldSum == gameTable.getTableHeight() * gameTable.getTableWidth()) {
			return true;
		}
		return gameTable.checkIfGameFinished();
	}
	
	public Point getCurrentPlayerPos() {
		return currentPlayer.getPlayerOriginPlace();
	}
	
	public List<Colors> getAvailableColorsForCurrentPlayer() {
		List<Colors> listOfAvailableColors = new ArrayList<Colors>();
		Collections.addAll(listOfAvailableColors, Colors.values());
		
		listOfAvailableColors.remove(Colors.BLACK);
		for (Player player : listOfPlayers) {
			listOfAvailableColors.remove(player.getPlayerColor());
		}
		return listOfAvailableColors;
	}
	
	public Collection<Colors> getCurrentTable() {
		return gameTable.getCurrentTable();
	}

	public int getTableWidth() {
		return gameTable.getTableWidth();
	}
	
	/**
	 * Zwraca indeks zwyciezcy, jesli null - gra wciaz trwa.
	 * 
	 * @return indeks gracza lub null, jesli gra wciaz trwa
	 */
	public Integer getWinner() {
		if (isGameFinished()) {
			int maxFields = 0;
			Player playerToWin = null;
			for (Player player : listOfPlayers) {
				if (player.getNumberOfFieldsTakenByPlayer() > maxFields) {
					maxFields = player.getNumberOfFieldsTakenByPlayer();
					playerToWin = player; 
				}
			}
			return listOfPlayers.indexOf(playerToWin);
		}
		return null;
	}

	public Integer getCurrentPlayerId() {
		return currentPlayer.getPlayerId();
	}

	public Integer getCurrentPlayerTakenFieldsNumber() {
		return currentPlayer.getNumberOfFieldsTakenByPlayer();
	}
}