package game_player;

import game_utils.Colors;

/**
 * Interfejs dla klas reprezentujących graczy.
 * 
 * @author Mateusz Kamiński
 */
public interface Player {
	
	/**
	 * Wykonuje ruch, aktualizując stół gry.
	 */
	public void makeMove();
	
	/**
	 * Ustawia wybrany przez gracza kolor w danej turze.
	 * 
	 * @param chosenColor wybrany kolor
	 */
	public void setChosenColor(Colors chosenColor);
	
	/**
	 * Zwraca aktualny kolor gracza.
	 * 
	 * @return kolor gracza
	 */
	public Colors getPlayerColor();
	
	/**
	 * Zwraca liczbę pól zajętych przez gracza.
	 * 
	 * @return liczba pól
	 */
	public Integer getNumberOfFieldsTakenByPlayer();
	
	/**
	 * Zwraca początkową pozycję gracza.
	 * 
	 * @return punkt początkowy
	 */
	public Integer getPlayerOriginPlace();
	
	/**
	 * Zwraca ID gracza.
	 * 
	 * @return ID gracza
	 */
	public Integer getPlayerId();
}