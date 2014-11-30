package statistical_module;

import game_main.IncorrectColorException;

import java.util.Map;

/**
 * Interfejs operacji, które można wykonać za pomocą modułu statystycznego.
 * 
 * @author Mateusz Kamiński
 */
public interface StatisticalOperation {
	
	/**
	 * Zwraca nazwę statystycznej operacji.
	 * 
	 * @return nazwa operacji
	 */
	String getOperationName();
	
	/**
	 * Przeprowadza wykonanie operacji statystycznej.
	 * 
	 * @throws IncorrectColorException 
	 */
	void run() throws IncorrectColorException;
	
	/**
	 * Zwraca mapę informacji specyficznych dla danej operacji.
	 * 
	 * @return mapa informacji
	 */
	Map<String, String> operationResult();
	
	/**
	 * Zwraca czas wykonania operacji w milisekundach.
	 * 
	 * @return czas w milisekundach
	 */
	Long getRunningTime();
}
