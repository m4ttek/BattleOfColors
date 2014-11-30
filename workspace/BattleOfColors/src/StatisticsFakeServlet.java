import game_main.IncorrectColorException;

import java.util.List;
import java.util.Map;

import statistical_module.MinMaxVSMinMaxStatisticalOperation;
import statistical_module.StatisticalOperation;
import statistical_module.StatisticsPerformer;

/**
 * Testuje moduł statystyk.
 * 
 * @author Mateusz Kamiński
 */
public class StatisticsFakeServlet {

	public static void main(String[] args) {
		StatisticsPerformer performer = StatisticsPerformer.getPerfomer();
		int numberOfGamesToBePlayer = 1000;
		for (int i = 0; i < numberOfGamesToBePlayer; i++) {
			performer.addStatisticalOperation(new MinMaxVSMinMaxStatisticalOperation(15, 1, 3));
		}
		try {
			performer.runAllStatisticalOperations();
		} catch (IncorrectColorException e) {
			e.printStackTrace();
			return;
		}
		List<StatisticalOperation> readyOperations = performer.getReadyOperations();
		int operationNumber = 0;
		int player1wins = 0;
		int player2wins = 0;
		int wholeMoveTime = 0;
		int wholeNumberOfMoves = 0;
		long wholeGameTime = 0;
		
		for (StatisticalOperation statisticalOperation : readyOperations) {
			statisticalOperation.getRunningTime();
			Map<String, String> operationResult = statisticalOperation.operationResult();
			String winnerId = operationResult.get("winner");
			String meanMoveTime = operationResult.get("meanMoveTime");
			String numberOfOverallMoves = operationResult.get("overallMoves");
			String fastestMove = operationResult.get("fastestMove");
			String slowestMove = operationResult.get("slowestMove");
			System.out.println("#op" + operationNumber + " winner = "
					+ winnerId + " mean move time = " + meanMoveTime
					+ "ms\noverall number of moves = " + numberOfOverallMoves
					+ " fastest move = " + fastestMove + "ms slowest move = "
					+ slowestMove + "ms game time = " + statisticalOperation.getRunningTime());
			if (winnerId.equals("0")) {
				player1wins++;
			} else {
				player2wins++;
			}
			wholeMoveTime += Integer.valueOf(meanMoveTime);
			wholeNumberOfMoves += Integer.valueOf(numberOfOverallMoves);
			wholeGameTime += statisticalOperation.getRunningTime();
			operationNumber++;
		}
		System.out.println("\n\nSummary: \nPlayer 1 won " + player1wins
				+ " times\nPlayer 2 won " + player2wins
				+ " times\nMean game time " + (float) wholeGameTime / numberOfGamesToBePlayer + "\nMean move time " + (float) wholeMoveTime
				/ numberOfGamesToBePlayer + "ms\nMean number of moves " + (float) wholeNumberOfMoves / numberOfGamesToBePlayer);
	}
}
