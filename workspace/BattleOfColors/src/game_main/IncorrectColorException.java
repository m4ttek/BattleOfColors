package game_main;

import game_utils.Colors;

/**
 * Wyjątek rzucany w przypadku wybrania niepoprawnego koloru przez gracza w trakcie jego ruchu.
 * 
 * @author Mateusz Kamiński
 */
public class IncorrectColorException extends Exception {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6842437809701040754L;
	
	private final Integer playerId;

	private final Colors color;
	
	public IncorrectColorException(Integer playerId, Colors color) {
		this.playerId = playerId;
		this.color = color;
	}

	public Integer getPlayerId() {
		return playerId;
	}

	public Colors getColor() {
		return color;
	}
	
	@Override
	public String getMessage() {
		return "Incorrect chosen color " + getColor() + " for player with id: " + getPlayerId();
	}
}
