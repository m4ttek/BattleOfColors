package controller;

import game_main.IncorrectColorException;
import game_utils.PlayerType;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import statistical_module.OperationConfig;
import statistical_module.StatisticalOperation;
import statistical_module.StatisticalOperationFactory;
import statistical_module.StatisticsPerformer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet obsługujący żądania do modułu statystycznego.
 * 
 * @author Mateusz Kamiński
 */
@WebServlet(description = "Servlet called when a user wants to calculate statistcs about game.", urlPatterns = { "/StatisticsServlet" }, asyncSupported = true)
public class StatisticsServlet extends HttpServlet {

	public static enum REQUEST_STATES {

		START("start"), CALCULATING("calculating"), COMPLETE("complete");

		private final String nazwa;

		REQUEST_STATES(String nazwa) {
			this.nazwa = nazwa;
		}

		public final String getNazwa() {
			return nazwa;
		}
	}

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 6546005031360199757L;

	/**
	 * Napis identyfikujący polecenie ajax'owe oznaczające rozpoczęcie obli
	 */
	public static final String STATISTICS_START_PREFIX = "caluculate_statistics";

	private static final ExecutorService executor = Executors.newFixedThreadPool(100);

	/**
	 * Logger zdarzeń.
	 */
	private static final Logger logger = Logger.getLogger("controller");
	
	/**
	 * Maksymalny czas życia asynchronicznego kontekstu.
	 */
	private static final long ASYNC_CONTEXT_LIVE_TIME = 10 * 60 * 1000;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {
		logger.entering("doGet", "StatisticsServlet");
		httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
		httpServletResponse.setHeader("Connection", "keep-alive");
		httpServletResponse.setContentType("application/json");
		AsyncContext asyncContext;

		String state = httpServletRequest.getParameter("state");
		if (state == null) {
			logger.log(Level.WARNING, "Stan przysłany od klienta to null!");
		} else {
			logger.log(Level.INFO, "Stan przysłany od klienta: " + state);
		}
		
		//PrintWriter writer = httpServletResponse.getWriter();
		if (REQUEST_STATES.START.getNazwa().equals(state)) {
			logger.log(Level.INFO, "Wystartowanie nowego wątku obliczeniowego");
			asyncContext = httpServletRequest.startAsync(httpServletRequest,httpServletResponse);
			asyncContext.setTimeout(ASYNC_CONTEXT_LIVE_TIME);
			executor.execute(new StatisticCalculations(asyncContext));
			
			logger.log(Level.INFO, "Odesłanie informacji do klienta: " + REQUEST_STATES.CALCULATING.getNazwa());
			//writer.print("\"" + REQUEST_STATES.CALCULATING.getNazwa() + "\"");
		} else if (REQUEST_STATES.CALCULATING.getNazwa().equals(state)) {
			if (httpServletRequest.getAsyncContext() != null) {
				logger.log(Level.INFO, "Obliczanie nie skończyło się");
				//writer.print("\"" + REQUEST_STATES.CALCULATING.getNazwa() + "\"");
			} else {
				logger.log(Level.INFO, "Obliczanie zakończone");
				//writer.print("\"" + REQUEST_STATES.COMPLETE.getNazwa() + "\"");
			}
		} else if (REQUEST_STATES.COMPLETE.getNazwa().equals(state)) {
			if (httpServletRequest.getAsyncContext() != null) {
				logger.log(Level.INFO, "Obliczanie zakończone - odebrano potwierdzenie od użytkownika");
			} else {
				httpServletResponse
						.sendError(HttpServletResponse.SC_BAD_REQUEST);
			}
			//writer.print("\"" + REQUEST_STATES.COMPLETE.getNazwa() + "\"");
		} else {
			logger.log(Level.WARNING, "Stan przysłany od klienta nie jest obsługiwany: " + state);
		}
		
		//writer.close();
		logger.exiting("doGet", "StatisticsServlet");
	}
	
	public class StatisticCalculations implements Runnable {

		private final AsyncContext asyncContext;
		
		public StatisticCalculations(final AsyncContext asyncContext) {
			this.asyncContext = asyncContext;
		}

		@Override
		public void run() {
			logger.entering(StatisticCalculations.class.getName(), "run");
			Integer numberOfGames = 0;
			ServletRequest request = asyncContext.getRequest();
			numberOfGames = Integer.valueOf(request
					.getParameter("numberOfGames"));
			StatisticsPerformer performer = StatisticsPerformer.getPerfomer();
			StatisticalOperationFactory statisticalOperationFactory = StatisticalOperationFactory
					.getStatisticalOperationFactory();
			OperationConfig config = prepareConfiguration();
			for (int i = 0; i < numberOfGames; i++) {
				performer.addStatisticalOperation(statisticalOperationFactory
						.produce(config));
			}
			try {
				logger.log(Level.INFO, "Wykonywanie obliczeń statystycznych...");
				performer.runAllStatisticalOperations();
				logger.log(Level.INFO, "Zakończone wykonywanie obliczeń.");
			} catch (IncorrectColorException e) {
				logger.log(Level.SEVERE, "Błąd przy wykonywaniu statystyk - rzucony wyjątek IncorrectColorException" + e.getMessage());
				asyncContext.complete();
				return;
			}
			logger.log(Level.INFO, "Pobranie gotowych operacji");
			List<StatisticalOperation> readyOperations = performer.getReadyOperations();
			if (readyOperations.size() != numberOfGames) {
				logger.log(Level.WARNING, "Liczba pobranych operacji różna od określonych przez klienta!");
			}
			
			int operationNumber = 0;
			int player1wins = 0;
			int player2wins = 0;
			int wholeMoveTime = 0;
			int wholeNumberOfMoves = 0;
			long wholeGameTime = 0;
			
			Gson gson = new GsonBuilder().create();
			PrintWriter writer;
			try {
				writer = asyncContext.getResponse().getWriter();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Statistic servlet error while writing");
			}
			logger.log(Level.INFO, "Wyznaczenie statystyk dla klienta.");
			
			Map<Integer, Map<String, String>> gamesResults = new HashMap<Integer, Map<String, String>>();
			for (StatisticalOperation statisticalOperation : readyOperations) {
				Map<String, String> operationResult = statisticalOperation.operationResult();
				
				String winnerId = operationResult.get("winner");
				String meanMoveTime = operationResult.get("meanMoveTime");
				String numberOfOverallMoves = operationResult.get("overallMoves");
				String firstPlayerMoveTimes = operationResult.get("firstPlayerMoveTimes");
				String secondPlayerMoveTimes = operationResult.get("secondPlayerMoveTimes");
				if (winnerId.equals("0")) {
					player1wins++;
				} else {
					player2wins++;
				}
				try {
					wholeMoveTime += Integer.valueOf(meanMoveTime);
					wholeNumberOfMoves += Integer.valueOf(numberOfOverallMoves);
				} catch (NumberFormatException exception) {
					logger.log(Level.WARNING, "Rzutowanie na liczbę nie powiodło się.");
					return;
				}
				wholeGameTime += statisticalOperation.getRunningTime();
				operationNumber++;
				gamesResults.put(operationNumber, operationResult);
			}
			
			Map<String, String> overallGameStatistics = new HashMap<String, String>();
			if (operationNumber != 0) {
				overallGameStatistics.put("player1wins", String.valueOf(player1wins));
				overallGameStatistics.put("player2wins", String.valueOf(player2wins));
				overallGameStatistics.put("wholeGameTime", String.valueOf(wholeGameTime));
				overallGameStatistics.put("wholeMeanMoveTime", String.valueOf((double) wholeMoveTime/ numberOfGames));
				overallGameStatistics.put("wholeMeanNumberOfMoves", String.valueOf((double) wholeNumberOfMoves/ numberOfGames));
			}
			gamesResults.put(0, overallGameStatistics);
			logger.log(Level.INFO, "Wygenerowany json: " + gson.toJson(gamesResults));
			writer.println(gson.toJson(gamesResults));
			writer.flush();
			asyncContext.complete();
			logger.exiting(StatisticCalculations.class.getName(), "run");
		}
		
		/**
		 * Przygotowuje konfigurację dla przeprowadzanych obliczeń.
		 * 
		 * @return
		 */
		private OperationConfig prepareConfiguration() {
			OperationConfig config = null;
			ServletRequest request = asyncContext.getRequest();
			try {
				config = new OperationConfig(PlayerType.getPlayerType(request.getParameter("player1")), 
						PlayerType.getPlayerType(request.getParameter("player2")),
						Integer.valueOf(request.getParameter("level1")),
						Integer.valueOf(request.getParameter("level2")),
						Integer.valueOf(request.getParameter("size")));
				
			} catch (NumberFormatException e) {
				logger.log(Level.WARNING, "Wystąpił NumberFormatException przy konwersji parametrów pobranych od klienta!");
				asyncContext.complete();
				return null;
			}
			logger.log(Level.WARNING, "Utworzona konfiguracja statystyk: " + config);
			return config;
		}
	}
}