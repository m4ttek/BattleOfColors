package statistical_module;

import game_utils.PlayerType;

/**
 * Klasa bean'owa dla z konfiguracją dla fabryki produkującej statystyki.
 * 
 * @author Mateusz Kamiński
 */
public class OperationConfig {

	private PlayerType playerType1;
	private PlayerType playerType2;
	private int level1;
	private int level2;
	private int size;
	
	public OperationConfig(PlayerType player1, PlayerType player2, int level1,
			int level2, int size) {
		super();
		this.playerType1 = player1;
		this.setPlayerType2(player2);
		this.setLevel1(level1);
		this.setLevel2(level2);
		this.setSize(size);
	}

	public PlayerType getPlayerType1() {
		return playerType1;
	}

	public void setPlayer1(PlayerType player1) {
		this.setPlayerType2(player1);
	}

	public PlayerType getPlayerType2() {
		return playerType2;
	}

	public void setPlayerType2(PlayerType playerType2) {
		this.playerType2 = playerType2;
	}

	public int getLevel1() {
		return level1;
	}

	public void setLevel1(int level1) {
		this.level1 = level1;
	}

	public int getLevel2() {
		return level2;
	}

	public void setLevel2(int level2) {
		this.level2 = level2;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("First player type: ").append(playerType1)
					.append(" with level ").append(level1).append('\n')
					.append("Second player type: ").append(playerType2)
					.append(" with level ").append(level2).append('\n')
					.append("Map size: ").append(size);
		return stringBuilder.toString();
	}
}
