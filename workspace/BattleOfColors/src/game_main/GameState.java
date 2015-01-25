package game_main;

import game_player.AIPlayer;
import game_player.AIPlayerAlfaBeta;
import game_player.AIPlayerMinMax;
import game_player.DefaultPlayer;
import game_player.Player;
import game_table.DefaultGameTable;
import game_table.GameTable;
import game_utils.Colors;
import game_utils.GameAction;
import game_utils.PlayerType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Klasa zawierająca stan gry i możliwe operacje.
 * 
 * @author Mateusz Kamiński
 */
public class GameState {
	
	static Logger log = Logger.getLogger("GameState");
	
	private static final int DEFAULT_STARTING_PLAYER = 1;
	
	private GameAction gameAction=GameAction.LOAD;
	
	//private static GameState gameState;

	private List<Player> listOfPlayers;
	
	private GameTable gameTable;
	
	private Player currentPlayer;
	
	private int playerIdx;
	
	private int turn=0;

	private boolean restart=false;
	
	private GameState(List<PlayerType> players,int size, int startingPlayer) {
		gameTable = new DefaultGameTable(null,size);
		listOfPlayers = new ArrayList<Player>();
		
		Collection<Integer> playersStartingPoints = gameTable.getPlayersStartingPoints();
		Iterator<Integer> iterator = playersStartingPoints.iterator();
		
		int playerNo = 0;
		for (PlayerType playerType : players) {
			Integer playerPos = iterator.next();
			if (playerType.equals(PlayerType.AI_MIN_MAX)) {
				if(listOfPlayers.isEmpty()) {
					listOfPlayers.add(new AIPlayerMinMax(gameTable, playerPos, this, playerNo+1, 4));
				}
				else {
					listOfPlayers.add(new AIPlayerMinMax(gameTable, playerPos, this, playerNo-1, 4));
				}
			} 
			else if (playerType.equals(PlayerType.AI_ALFA_BETA)) {
				if(listOfPlayers.isEmpty()) {
					listOfPlayers.add(new AIPlayerAlfaBeta(gameTable, playerPos, this, playerNo+1, 4));
				}
				else {
					listOfPlayers.add(new AIPlayerAlfaBeta(gameTable, playerPos, this, playerNo-1, 4));
				}
			}
			else if (playerType.equals(PlayerType.HUMAN)) {
				listOfPlayers.add(new DefaultPlayer(gameTable, playerPos));
			}
			playerNo++;
		}
		currentPlayer = listOfPlayers.get(startingPlayer-1);
		playerIdx = startingPlayer%2;
		turn=startingPlayer;
	}
	
	public void setPlayerDifficultyLevel(int playerId, int level) {
		for(Player player : listOfPlayers) {
			if(player.getPlayerId() == playerId) {
				if(player instanceof AIPlayer) {
					int playerLevel;
					if(level == 1) {
						playerLevel = 1;
					}
					else if(level == 2) {
						playerLevel = 2;
					}
					else if(level == 3) {
						playerLevel = 4;
					}
					else {
						playerLevel = 6;
					}
					((AIPlayer) player).setDifficultyLevel(playerLevel);
				}
				return;
			}
		}
	}
	
	public static GameState startGame(List<PlayerType> playerTypeList,int size) {
		return startGame(playerTypeList, size, DEFAULT_STARTING_PLAYER);
	}
	
	public static GameState startGame(List<PlayerType> playerTypeList,int size, int startingPlayer) {
		/*if (gameState == null) {
			gameState = new GameState(playerTypeList,size);
		}
		return gameState;*/
		DefaultPlayer.reset();
		return new GameState(playerTypeList,size, startingPlayer);
	}
	
	public static GameState restartGame(List<PlayerType> playerTypeList,int size) {
		return restartGame(playerTypeList, size, DEFAULT_STARTING_PLAYER);
	}
	
	public static GameState restartGame(List<PlayerType> playerTypeList,int size, int startingPlayer) {
		//gameState = null;
		return GameState.startGame(playerTypeList,size, startingPlayer);
	}
	
	public void makeNextMove(String moveParameters) throws IncorrectColorException {
		// jeśli gra się zakończyła, nie wykonujemy już żadnego ruchu
		if (isGameFinished()) {
			return;
		}
		
		if(moveParameters != null) {
			Colors chosenColor = Colors.valueOf(moveParameters);
			if (!getAvailableColorsForCurrentPlayer().contains(chosenColor)) {
				throw new IncorrectColorException(currentPlayer.getPlayerId(), chosenColor);
			}
			currentPlayer.setChosenColor(chosenColor);
		}
		currentPlayer.makeMove();
		currentPlayer = listOfPlayers.get(playerIdx++);
		if (playerIdx % listOfPlayers.size() == 0) {
			playerIdx = 0;
		}
		turn = 1+(turn)%2;
	}
	
	public Colors getOtherPlayersColor(Integer playerId) {
		if(listOfPlayers.get(0).getPlayerId() == playerId) {
			return listOfPlayers.get(1).getPlayerColor();
		}
		else {
			return listOfPlayers.get(0).getPlayerColor();
		}
	}
	
	public boolean isGameFinished() {
		int fieldSum = 0;
		// Na początku wykonujemy proste sprawdzenie czy suma pól zajętych przez graczy równa się rozmiarowi stołu.
		for (Player player : listOfPlayers) {
			fieldSum += player.getNumberOfFieldsTakenByPlayer();
		}
		if (fieldSum == gameTable.getTableHeight() * gameTable.getTableWidth()) {
			return true;
		}
		return gameTable.checkIfGameFinished();
	}
	
	public Integer getCurrentPlayerPos() {
		return currentPlayer.getPlayerOriginPlace();
	}
	
	/**
	 * Zwraca listę dozwolonych kolorów dla gracza w danym ruchu.
	 * 
	 * @return lista kolorów
	 */
	public List<Colors> getAvailableColorsForCurrentPlayer() {
		List<Colors> listOfAvailableColors = new ArrayList<Colors>();
		Collections.addAll(listOfAvailableColors, Colors.values());
		
		listOfAvailableColors.remove(Colors.BLACK);
		for (Player player : listOfPlayers) {
			listOfAvailableColors.remove(player.getPlayerColor());
		}
		return listOfAvailableColors;
	}
	
	public Map<Integer, Colors> getCurrentTable() {
		return gameTable.getCurrentTable();
	}
	public int getTurn() {
		return turn;
	}
	public int getTableWidth() {
		return gameTable.getTableWidth();
	}
	public int getTableHeight() {
		return gameTable.getTableHeight();
	}
	
	/**
	 * Zwraca indeks zwyciężcy, jesli null - gra wciaz trwa.
	 * 
	 * @return indeks gracza lub null, jesli gra wciaz trwa
	 */
	public Integer getWinner() {
		if (isGameFinished()) {
			int maxFields = -1;
			Player playerToWin = null;
			for (Player player : listOfPlayers) {
				if (player.getNumberOfFieldsTakenByPlayer() > maxFields) {
					maxFields = player.getNumberOfFieldsTakenByPlayer();
					playerToWin = player; 
				}
				else if(player.getNumberOfFieldsTakenByPlayer() == maxFields) {
					//draw
					return -1;
				}
			}
			return listOfPlayers.indexOf(playerToWin);
		}
		return null;
	}

	public Integer getCurrentPlayerId() {
		return currentPlayer.getPlayerId();
	}
	public Integer getPreviousPlayerId() {
		return listOfPlayers.get(playerIdx).getPlayerId();
	}

	public Integer getCurrentPlayerTakenFieldsNumber() {
		return currentPlayer.getNumberOfFieldsTakenByPlayer();
	}
	public Integer getPreviousPlayerTakenFieldsNumber() {
		return listOfPlayers.get(playerIdx).getNumberOfFieldsTakenByPlayer();
	}
	public Integer getPlayerTakenFieldsNumber(int id) {
		return listOfPlayers.get(id).getNumberOfFieldsTakenByPlayer();
	}
	/**
	 * Zwraca typ aktualnego gracza
	 * 
	 * @return typ aktualnego gracza
	 */
	public PlayerType getCurrentPlayerType() {
		if(currentPlayer instanceof AIPlayerMinMax){
			return PlayerType.AI_MIN_MAX;
		}
		else if(currentPlayer instanceof AIPlayerAlfaBeta){
			return PlayerType.AI_ALFA_BETA;
		}
		else
			return PlayerType.HUMAN;
	}

	public GameAction getGameAction() {
		return gameAction;
	}

	public void setGameAction(GameAction gameAction) {
		this.gameAction = gameAction;
	}

	public boolean isRestart() {
		return restart;
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
	}
}