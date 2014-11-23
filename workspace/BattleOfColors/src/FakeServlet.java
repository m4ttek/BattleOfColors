import game_main.GameState;
import game_main.IncorrectColorException;
import game_utils.Colors;
import game_utils.PlayerType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * Klasa testująca grę.
 * 
 * @author Mateusz Kamiński
 */
public class FakeServlet {

	private static final PlayerType[] playerTypes = { PlayerType.HUMAN, PlayerType.HUMAN};
	
	public static void main(String[] args) throws IncorrectColorException {
		System.out.println("Battle of Colors\n");
		GameState gameState = GameState.startGame(Arrays.asList(playerTypes[0], playerTypes[1]));
		gameState.setPlayerDifficultyLevel(0, 3);
		gameState.setPlayerDifficultyLevel(1, 4);
		Scanner scanner = new Scanner(System.in);
		while (!gameState.isGameFinished()) {
			System.out.println("Gracz " + gameState.getCurrentPlayerId() + " posiada " + gameState.getCurrentPlayerTakenFieldsNumber() + " pól.");
			drawGameTable(gameState.getCurrentTable(), gameState.getTableWidth());
			if(playerTypes[gameState.getCurrentPlayerId()] == PlayerType.HUMAN) {
				System.out.println("Human move, player no. " + gameState.getCurrentPlayerId());
				drawColorsForPlayer(gameState.getAvailableColorsForCurrentPlayer());
				gameState.makeNextMove(scanner.nextLine());
			}
			else {
				//activate line below to stop after each move in AI-AI game
				//scanner.nextLine();
				gameState.makeNextMove(null);
			}
		}
		System.out.println("Wygrał gracz o id: " + gameState.getWinner());
		scanner.close();
		System.exit(gameState.getWinner());
	}

	private static void drawColorsForPlayer(List<Colors> availableColorsForCurrentPlayer) {
		StringBuilder playerColors = new StringBuilder("Wybierz kolor:\n|");
		for (Colors playerColor : availableColorsForCurrentPlayer) {
			playerColors.append(playerColor).append("|");
		}
		System.out.println(playerColors.toString());
	}

	private static void drawGameTable(Collection<Colors> currentTable, Integer tableWidth) {
		StringBuilder gameColors = new StringBuilder();
		for (int i = 0; i < tableWidth; i++) {
			gameColors.append("-----");
		}
		gameColors.append("\n");
		int x = 0;
		for (Colors tableColor: currentTable) {
			gameColors.append("|").append(tableColor.getColorName().substring(0, 3)).append("|");
			x++;
			if (x == tableWidth) {
				gameColors.append("\n");
				x = 0;
			}
		}
		for (int i = 0; i < tableWidth; i++) {
			gameColors.append("-----");
		}
		System.out.println(gameColors.toString());
	}
}
