package game_utils;

/**
 * Typ wyliczeniowy dla kolorów w grze.
 * 
 * @author Mateusz Kamiński
 */
public enum Colors {
	BLACK("Black"),
	GREEN("Green"),
	YELLOW("Yellow"),
	RED("Red"),
	BROWN("Brown"),
	PINK("Pink"),
	BLUE("Blue");
	
	private String colorName;
	
	private Colors(String colorName) {
		this.colorName = colorName;
	}
	
	public String getColorName() {
		return colorName;
	}
}