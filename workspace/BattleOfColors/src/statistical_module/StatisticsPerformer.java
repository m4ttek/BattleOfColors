package statistical_module;

import game_main.IncorrectColorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa zawierająca operacje umożliwiająca wykonanie statystyk dotyczących gry.
 * 
 * @author Mateusz Kamiński
 */
public class StatisticsPerformer {
	
	private static StatisticsPerformer performStatistics;
	
	private List<StatisticalOperation> listOfStatisticsToBeDone;
	
	private List<StatisticalOperation> listOfFinishedStatistics;
	
	public static StatisticsPerformer getPerfomer() {
		if (performStatistics == null) {
			performStatistics = new StatisticsPerformer();
			performStatistics.reset();
		}
		return performStatistics;
	}
	
	public void addStatisticalOperation(StatisticalOperation statisticalOperation) {
		listOfStatisticsToBeDone.add(statisticalOperation);
	}
	
	public void addListOfStatisticalOperations(List<StatisticalOperation> statisticalOperations) {
		listOfStatisticsToBeDone.addAll(statisticalOperations);
	}
	
	public void runAllStatisticalOperations() throws IncorrectColorException {
		try {
			for (StatisticalOperation statisticalOperation : listOfStatisticsToBeDone) {
				statisticalOperation.run();
				listOfFinishedStatistics.add(statisticalOperation);
			}
			listOfStatisticsToBeDone.clear();
		} catch(IncorrectColorException incorrectColorException) {
			reset();
			System.out.println("Error while proceeding statistical operations!");
			throw incorrectColorException;
		}
 	}
	
	/**
	 * Zwraca listę gotowych operacji statystycznych.
	 * 
	 * @return lista operacji
	 */
	public List<StatisticalOperation> getReadyOperations() {
		List<StatisticalOperation> returnList = listOfFinishedStatistics;
		listOfFinishedStatistics = new ArrayList<StatisticalOperation>();
		return returnList;
	}
	
	public void reset() {
		listOfFinishedStatistics = new ArrayList<StatisticalOperation>();
		listOfStatisticsToBeDone = new ArrayList<StatisticalOperation>();
	}
}
