package game_table;

import game_utils.Colors;

import java.awt.Point;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Interfejs dla stołów do gry.
 * 
 * @author Mateusz Kamiński
 */
public interface GameTable {

	/**
	 * Zwraca aktualną reprezentację stołu gry (nie historyczną).
	 * 
	 * @return kolekcja pól stołu
	 */
	public Map<Integer, Colors> getCurrentTable();

	/**
	 * Zwraca szerokość stołu.
	 * 
	 * @return
	 */
	public int getTableWidth();
	
	/**
	 * Zwraca wysokość stołu.
	 * 
	 * @return
	 */
	public int getTableHeight();
	
	/**
	 * Sprawdza czy gra się zakończyła.
	 * 
	 * @return
	 */
	public boolean checkIfGameFinished();
	
	/**
	 * Zwraca punkty startowe graczy.
	 * 
	 * @return
	 */
	public Collection<Point> getPlayersStartingPoints();
	
	/**
	 * Wykonuje hipotetyczny ruch dla gracza o danym ID.
	 * 
	 * @param playerId ID gracza
	 * @param color nowy kolor wypełnienia
	 * @return listę zajętych pól przez gracza
	 */
	public List<Integer> makeHypotheticalMove(Integer playerId, Colors color);
	
	/**
	 * 
	 * @param noOfMoves
	 */
	public void undoHypotheticalMove(int noOfMoves);
	
	/**
	 * Dostarcza historyczny stół na podstawie podanej liczbie poprzednich ruchów.
	 * 
	 * @param noOfMoves liczba poprzednich ruchów
	 * @return kolekcję pól reprezentujących tablicę
	 */
	public Map<Integer, Colors> getHistoricalTable(int noOfMoves);
	
	/**
	 * Zatwierdza wykonany ruch.
	 * 
	 * @param noOfMoves 
	 */
	public void acceptMove(int noOfMoves);

}
