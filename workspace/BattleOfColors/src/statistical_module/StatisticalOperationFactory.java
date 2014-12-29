package statistical_module;

import game_utils.PlayerType;

public class StatisticalOperationFactory {
	private static StatisticalOperationFactory statisticalOperationFactory;

	private StatisticalOperationFactory() {
		// TODO Auto-generated constructor stub
	}

	public static StatisticalOperationFactory getStatisticalOperationFactory() {
		if (statisticalOperationFactory == null) {
			statisticalOperationFactory = new StatisticalOperationFactory();
		}
		return statisticalOperationFactory;
	}

	public StatisticalOperation produce(OperationConfig config){
		if(PlayerType.AI_ALFA_BETA.equals(config.getPlayerType1()) &&
				PlayerType.AI_ALFA_BETA.equals(config.getPlayerType2())) {
			return new AlfaBetaVsAlfaBetaStatisticalOperation(config.getSize(), config.getLevel1(), config.getLevel2());
		} else if (PlayerType.AI_MIN_MAX.equals(config.getPlayerType1()) &&
				PlayerType.AI_MIN_MAX.equals(config.getPlayerType2())) {
			return new MinMaxVSMinMaxStatisticalOperation(config.getSize(), config.getLevel1(), config.getLevel2());
		} else if (PlayerType.AI_ALFA_BETA.equals(config.getPlayerType1())) {
			return new AlfaBetaVsMinMaxStatisticalOperation(config.getSize(), config.getLevel1(), config.getLevel2());
		} else {
			return null;
		}
	}
}