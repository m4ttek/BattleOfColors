import game_main.GameState;
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
	
	public static void main(String[] args) {
		System.out.println("Battle of Colors\n");
		
		GameState gameState = GameState.startGame(Arrays.asList(PlayerType.HUMAN, PlayerType.HUMAN));
		Scanner scanner = new Scanner(System.in);
		while (!gameState.isGameFinished()) {
			drawGameTable(gameState.getCurrentTable(), gameState.getTableWidth());
			drawColorsForPlayer(gameState.getAvailableColorsForCurrentPlayer());
			gameState.makeNextMove(scanner.nextLine());
		}
		scanner.close();
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
