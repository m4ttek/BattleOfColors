package game_main;

import game_player.AIPlayer;
import game_player.DefaultPlayer;
import game_player.Player;
import game_table.DefaultGameTable;
import game_table.GameTable;
import game_utils.PlayerType;

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
	
	private GameState(List<PlayerType> players) {
		gameTable = new DefaultGameTable();
		
		for (PlayerType playerType : players) {
			if (playerType.equals(PlayerType.AI)) {
				listOfPlayers.add(new AIPlayer(gameTable));
			} else if (playerType.equals(PlayerType.HUMAN)) {
				listOfPlayers.add(new DefaultPlayer(gameTable));
			}
		}
	}
	
	public static GameState startGame(List<PlayerType> playerTypeList) {
		if (gameState != null) {
			gameState = new GameState(playerTypeList);
		}
		return gameState;
	}
	
	public static GameState restartGame(List<PlayerType> playerTypeList) {
		gameState = null;
		return GameState.startGame(playerTypeList);
	}
	
	public void makeNextMove(String moveParameters) {
		
	}
}