package statistical_module;

import game_main.GameState;
import game_main.IncorrectColorException;
import game_utils.PlayerType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasa przeprowadzająca operację statystyczną.
 * 
 * @author Mateusz Kamiński
 */
public class AlfaBetaVsMinMaxStatisticalOperation implements StatisticalOperation {

	public static final String NAME = "AlfaBetaVSMinMax";

	private final GameState gameState;

	private long startTime;

	private long endTime;

	private Integer winnerId;

	private int numberOfOverallMoves;
	
	private List<Long> moveRunningTimes = new ArrayList<Long>();

	private List<Long> firstPlayerMoveRunningTimes = new ArrayList<Long>();
	
	private List<Long> secondPlayerMoveRunningTimes = new ArrayList<Long>();
	
	private Long meanMoveTime;
	
	private Long firstPlayerMeanMoveTime = 0L;
	
	private Long secondPlayerMeanMoveTime = 0L;
	
	public AlfaBetaVsMinMaxStatisticalOperation(int mapSize, int firstPlayerDifficultyLevel,
			int secondPlayerDifficultyLevel) {
		List<PlayerType> players = new ArrayList<PlayerType>();
		players.add(PlayerType.AI_ALFA_BETA);
		players.add(PlayerType.AI_MIN_MAX);
		gameState = GameState.restartGame(players, mapSize);
		gameState.setPlayerDifficultyLevel(0, firstPlayerDifficultyLevel);
		gameState.setPlayerDifficultyLevel(1, secondPlayerDifficultyLevel);
	}

	@Override
	public String getOperationName() {
		return NAME;
	}

	@Override
	public void run() throws IncorrectColorException {
		startTime = System.nanoTime();
		
		long moveStartTime;
		long moveEndTime;
		boolean firstPlayerMove = true;
		while(!gameState.isGameFinished()) {
			moveStartTime = System.nanoTime();
			gameState.makeNextMove(null);
			moveEndTime = System.nanoTime();
			numberOfOverallMoves++;
			long timeDifference = moveEndTime - moveStartTime;
			if (firstPlayerMove) {
				firstPlayerMoveRunningTimes.add(timeDifference);
				firstPlayerMeanMoveTime += timeDifference;
			} else {
				secondPlayerMoveRunningTimes.add(timeDifference);
				secondPlayerMeanMoveTime += timeDifference;
			}
			firstPlayerMove = !firstPlayerMove;
			moveRunningTimes.add(timeDifference);
		}
		endTime = System.nanoTime();
		long wholeTime = 0;
		for (Long time : moveRunningTimes) {
			wholeTime += time;
		}
		System.out.println(wholeTime);
		meanMoveTime = wholeTime / numberOfOverallMoves;
		winnerId = gameState.getWinner();
	}

	@Override
	public Map<String, String> operationResult() {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("winner", winnerId.toString());
		resultMap.put("overallMoves", "" + numberOfOverallMoves);
		resultMap.put("meanMoveTime", meanMoveTime.toString());
		resultMap.put("fastestMove", Collections.min(moveRunningTimes).toString());
		resultMap.put("slowestMove", Collections.max(moveRunningTimes).toString());
		resultMap.put("firstPlayerMeanMoveTime", String.valueOf(((double) firstPlayerMeanMoveTime / firstPlayerMoveRunningTimes.size())));
		resultMap.put("secondPlayerMeanMoveTime", String.valueOf(((double) secondPlayerMeanMoveTime / secondPlayerMoveRunningTimes.size())));
		resultMap.put("firstPlayerMoveTimes", firstPlayerMoveRunningTimes.toString());
		resultMap.put("secondPlayerMoveTimes", secondPlayerMoveRunningTimes.toString());
		return resultMap;
	}

	@Override
	public Long getRunningTime() {
		return endTime - startTime;
	}
}
