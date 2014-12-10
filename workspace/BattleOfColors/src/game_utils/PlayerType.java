package game_utils;

/**
 * Typ wyliczeniowy dla typu gracza.
 * 
 * @author Mateusz Kamiński
 */
public enum PlayerType {
	HUMAN,
	AI_MIN_MAX,
	AI_ALFA_BETA;
	
	public static PlayerType getPlayerType(String type) {
		type = type.toLowerCase();
		switch (type) {
		case "human":
			return HUMAN;
		case "ai_min_max":
			return AI_MIN_MAX;
		case "ai_alfa_beta":
			return AI_ALFA_BETA;
		default:
			return null;
		}
	}
}