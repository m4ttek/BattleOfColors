package game_player;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game_main.GameState;
import game_table.GameTable;
import game_utils.Colors;

/**
 * Klasa gracza grającego według algorytmu MIN-MAX
 * @author Michał Pluta
 *
 */
public class AIPlayerMinMax extends AIPlayer {

	/**
	 * Zmienna określająca ile ruchów wprzód analizuje algorytm min-max
	 */
	private int depthLevel;
	
	private static Random RAND = new Random();
	
	public AIPlayerMinMax(GameTable gameTable, Point playerStartingPosition,
			GameState gameState, Integer opponentsPlayerId, int depthLevel) {
		super(gameTable, playerStartingPosition, gameState, opponentsPlayerId);
		this.depthLevel = depthLevel;
	}
	
	/**
	 * Metoda ustawia liczbę analizowanych ruchów algorytmu.
	 * Nie należy ustawiać wartości większych niż 5
	 */
	@Override
	public void setDifficultyLevel(int level) {
		if(level < 1) level = 1;
		depthLevel = level;
	}
	
	@Override
	public void makeMove() {
		opponentsColor = gameState.getOtherPlayersColor(playerId);
		System.out.println("Other player's color is " + opponentsColor);
		List<Colors> availableColors = gameState.getAvailableColorsForCurrentPlayer();
		int bestResult = Integer.MIN_VALUE;
		Colors bestChoice = Colors.BLACK;
		for(Colors color : availableColors) {
			int moveValue = minMaxEvaluateMove(color, 0, availableColors, 0, 0, currentPlayerColor,
					opponentsColor);
			System.out.println(color + " would give me " + moveValue + " points.");
			if(moveValue > bestResult) {
				bestChoice = color;
				bestResult = moveValue;
			}
			else if(moveValue == bestResult) {
				if(RAND.nextInt(2) == 0) {
					bestChoice = color;
				}
			}
		}
		setChosenColor(bestChoice);
		takenFields = gameTable.makeHypotheticalMove(playerId, bestChoice);
		gameTable.acceptMove(0);
	}
	
	/**
	 * Metoda wywoływana rekurencyjnie zwracająca wartość wykonywanego ruchu (czyli
	 * różnicę między ilością kwadratów gracza, a ilością kwadratów przeciwnika
	 * @param tryColor - kolor, który testujemy
	 * @param movesMade - ilość ruchów wykonanych do tej pory (0 dla pierwszego wywołania metody)
	 * @param availableColors - dostępne kolory
	 * @param ourPoints - obecnie uzyskane punkty (kwadraty) gracza
	 * @param opponentsPoints - obecnie uzyskane punkty przeciwnika
	 * @param currentColor - aktualny kolor gracza
	 * @param opponentsCurrentColor - aktualny kolor przeciwnika
	 * @return wartość danego ruchu
	 */
	private int minMaxEvaluateMove(Colors tryColor, int movesMade, List<Colors> availableColors,
			int ourPoints, int opponentsPoints, Colors currentColor, Colors opponentsCurrentColor) {

		List<Colors> nextAvailableColors = new ArrayList<Colors>();
		nextAvailableColors.addAll(availableColors);
		
		if(movesMade % 2 == 0) {
			//our turn
			ourPoints = gameTable.makeHypotheticalMove(playerId, tryColor).size();
			//DEBUG
			/*for(int i = 0; i < movesMade; i++) {
				System.out.print("    ");
			}
			System.out.println("I get " + ourPoints + " points for picking " + tryColor);*/
			if(movesMade == depthLevel-1) {
				gameTable.undoHypotheticalMove(1);
				return ourPoints-opponentsPoints;
			}
			nextAvailableColors.remove(tryColor);
			if(currentColor != null && currentColor != Colors.BLACK) {
				nextAvailableColors.add(currentColor);
			}
			List<Integer> moveValues = new ArrayList<Integer>(4);
			for(Colors currentTryColor : nextAvailableColors) {
				moveValues.add(minMaxEvaluateMove(currentTryColor,
						movesMade+1, nextAvailableColors, ourPoints, opponentsPoints, tryColor, opponentsCurrentColor));
			}
			gameTable.undoHypotheticalMove(1);
			return min(moveValues);
		}
		else {
			//opponent's turn
			opponentsPoints = gameTable.makeHypotheticalMove(opponentsPlayerId, tryColor).size();
			//DEBUG
			/*for(int i = 0; i < movesMade; i++) {
				System.out.print("    ");
			}
			System.out.println("My opponent would get " + opponentsPoints + " points for picking " + tryColor);*/
			if(movesMade == depthLevel-1) {
				gameTable.undoHypotheticalMove(1);
				return ourPoints-opponentsPoints;
			}
			nextAvailableColors.remove(tryColor);
			if(opponentsCurrentColor != null && opponentsCurrentColor != Colors.BLACK) {
				nextAvailableColors.add(opponentsCurrentColor);
			}
			List<Integer> moveValues = new ArrayList<Integer>(4);
			for(Colors currentTryColor : nextAvailableColors) {
				moveValues.add(minMaxEvaluateMove(currentTryColor,
						movesMade+1, nextAvailableColors, ourPoints, opponentsPoints, currentColor, tryColor));
			}
			gameTable.undoHypotheticalMove(1);
			return max(moveValues);
		}
	}
	
	protected int max(List<Integer> numbers) {
		int maxValue = Integer.MIN_VALUE;
		for(Integer currentNumber : numbers) {
			if(currentNumber > maxValue) {
				maxValue = currentNumber;
			}
		}
		return maxValue;
	}
	
	protected int min(List<Integer> numbers) {
		int minValue = Integer.MAX_VALUE;
		for(Integer currentNumber : numbers) {
			if(currentNumber < minValue) {
				minValue = currentNumber;
			}
		}
		return minValue;
	}
	
}
