package game_player;

import game_main.GameState;
import game_table.GameTable;
import game_utils.Colors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Klasa gracza grającego według algorytmu ALFA-BETA
 * @author Michał Żakowski
 *
 */
public class AIPlayerAlfaBeta extends AIPlayer {

	/**
	 * Zmienna określająca ile ruchów wprzód analizuje algorytm alfa-beta
	 */
	private int depthLevel;
	
	private static Random RAND = new Random();
	
	public AIPlayerAlfaBeta(GameTable gameTable, Integer playerPos,
			GameState gameState, Integer opponentsPlayerId, int depthLevel) {
		super(gameTable, playerPos, gameState, opponentsPlayerId);
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

		List<Colors> availableColors = gameState.getAvailableColorsForCurrentPlayer();
		int bestResult = Integer.MIN_VALUE;
		Colors bestChoice = Colors.BLACK;

		for(Colors color : availableColors) {
			int moveValue = alfaBetaEvaluateMove(color, 0, availableColors, 0, 0, currentPlayerColor,
					opponentsColor,Integer.MIN_VALUE,Integer.MAX_VALUE);
			
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
	 * @param alfa - aktualna maksymalna wartosc osiagnieta przez nas
	 * @param beta - aktualna minimalna wartosc osiagnieta przez przeciwnika
	 * @return wartość danego ruchu
	 */
	private int alfaBetaEvaluateMove(Colors tryColor, int movesMade, List<Colors> availableColors,
			int ourPoints, int opponentsPoints, Colors currentColor, Colors opponentsCurrentColor,int alfa,int beta) {

		List<Colors> nextAvailableColors = new ArrayList<Colors>();
		nextAvailableColors.addAll(availableColors);
		
		if(movesMade % 2 == 0) {
			//our turn
			ourPoints = gameTable.makeHypotheticalMove(playerId, tryColor).size();

			if(movesMade == depthLevel-1) {
				gameTable.undoHypotheticalMove(1);
				return ourPoints-opponentsPoints;
			}
			nextAvailableColors.remove(tryColor);
			if(currentColor != null && currentColor != Colors.BLACK) {
				nextAvailableColors.add(currentColor);
			}
			for(Colors currentTryColor : nextAvailableColors) {
				int currentPoints = alfaBetaEvaluateMove(currentTryColor,
						movesMade+1, nextAvailableColors, ourPoints, opponentsPoints, tryColor, opponentsCurrentColor,
						alfa,beta);
				if(currentPoints<beta)
					beta=currentPoints;
				if(alfa>=beta){
					gameTable.undoHypotheticalMove(1);
					return alfa;
				}
			
			}
			gameTable.undoHypotheticalMove(1);
			return beta;
		}
		else {
			//opponent's turn
			//System.out.println(tryColor);
			opponentsPoints = gameTable.makeHypotheticalMove(opponentsPlayerId, tryColor).size();

			if(movesMade == depthLevel-1) {
				gameTable.undoHypotheticalMove(1);
				return ourPoints-opponentsPoints;
			}
			nextAvailableColors.remove(tryColor);
			if(opponentsCurrentColor != null && opponentsCurrentColor != Colors.BLACK) {
				nextAvailableColors.add(opponentsCurrentColor);
			}
			for(Colors currentTryColor : nextAvailableColors) {
				int currentPoints = alfaBetaEvaluateMove(currentTryColor,
						movesMade+1, nextAvailableColors, ourPoints, opponentsPoints, currentColor, tryColor,
						alfa,beta);
				
				if(currentPoints>alfa)
					alfa=currentPoints;
				if(alfa>=beta){
					gameTable.undoHypotheticalMove(1);
					return beta;
				}
			}	
			gameTable.undoHypotheticalMove(1);
			return alfa;
		}
	}
	
}
