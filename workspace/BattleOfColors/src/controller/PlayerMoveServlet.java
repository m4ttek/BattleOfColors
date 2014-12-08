package controller;

import game_main.GameState;
import game_main.IncorrectColorException;
import game_utils.Colors;
import game_utils.GameAction;
import game_utils.PlayerType;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class PlayerMoveServlet
 * @author Michał Pluta
 */
@WebServlet(description = "Servlet called when a player makes a move", urlPatterns = { "/PlayerMoveServlet" })
public class PlayerMoveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//private GameState gameState;
	//private GameAction action=GameAction.LOAD;
	//private boolean restart=false;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		GameState gameState = (GameState) session.getAttribute("gameState");
		GameAction action;
		if(gameState != null) {
			action = gameState.getGameAction();
		}
		else {
			action = GameAction.LOAD;
		}
		
		if(action==GameAction.LOAD && request.getParameter("player1")!=null){
			System.out.println("ASDzx");
			if(gameState != null && gameState.isRestart()){
				gameState = null;
				gameState=GameState.restartGame(Arrays.asList(PlayerType.valueOf(request.getParameter("player1")),PlayerType.valueOf(request.getParameter("player2"))),Integer.parseInt(request.getParameter("size")));	
				gameState.setRestart(false);
				session.setAttribute("gameState", gameState);
			}
			else {
				gameState=GameState.startGame(Arrays.asList(PlayerType.valueOf(request.getParameter("player1")),PlayerType.valueOf(request.getParameter("player2"))),Integer.parseInt(request.getParameter("size")));
				session.setAttribute("gameState", gameState);
			}
			gameState.setGameAction(GameAction.PLAY);
			
			if(PlayerType.valueOf(request.getParameter("player1")).equals(PlayerType.AI_MIN_MAX))
				gameState.setPlayerDifficultyLevel(0, Integer.parseInt(request.getParameter("level1")));
			if(PlayerType.valueOf(request.getParameter("player2")).equals(PlayerType.AI_MIN_MAX))
				gameState.setPlayerDifficultyLevel(1, Integer.parseInt(request.getParameter("level2")));

			if(((PlayerType.valueOf(request.getParameter("player1"))).equals(PlayerType.HUMAN) ||
				(PlayerType.valueOf(request.getParameter("player2"))).equals(PlayerType.HUMAN)) &&
				gameState.getCurrentPlayerType() != PlayerType.HUMAN) {
					
				try {
					gameState.makeNextMove(null);
				} catch (IncorrectColorException e) {
					e.printStackTrace();
				}
			}
			
			StringBuilder gameInfo=new StringBuilder();
			gameInfo.append(gameState.getCurrentPlayerType()+"+");
			gameInfo.append(gameState.getTableWidth()+"+"+gameState.getTableHeight()+"+");
			gameInfo.append(getAvailableColors(gameState));
			gameInfo.append("+");		
			gameInfo.append(getMapColors(gameState) + "+");
			gameInfo.append("0+");
			gameInfo.append("0+");
			gameInfo.append("1");
			PrintWriter outStream = response.getWriter();
			outStream.write("\""+gameInfo+"\"");
			outStream.close();
			return;
		}
		if(action == GameAction.PLAY && request.getParameter("size") != null) {
			//Continuing game
			System.out.println("Continuing game");
			
			int firstPlayerFields = gameState.getPlayerTakenFieldsNumber(0);
			int secondPlayerFields = gameState.getPlayerTakenFieldsNumber(1);
			
			StringBuilder gameInfo=new StringBuilder();
			gameInfo.append(gameState.getCurrentPlayerType()+"+");
			gameInfo.append(gameState.getTableWidth()+"+"+gameState.getTableHeight()+"+");
			gameInfo.append(getAvailableColors(gameState));
			gameInfo.append("+");		
			gameInfo.append(getMapColors(gameState) + "+");
			gameInfo.append(Integer.toString(firstPlayerFields) + "+");
			gameInfo.append(Integer.toString(secondPlayerFields) + "+");
			gameInfo.append(Integer.toString(gameState.getTurn()));
			PrintWriter outStream = response.getWriter();
			outStream.write("\""+gameInfo+"\"");
			outStream.close();
			return;
		}
		if(action==GameAction.PLAY) {
			System.out.println("xax");
			String chosenColor;
			boolean makeAiMove = false;
			
			if(gameState.getCurrentPlayerType().equals(PlayerType.AI_MIN_MAX))
				chosenColor=null;
			else
				chosenColor = request.getParameter("color");
			
			System.out.println("Gracz o id: "+gameState.getCurrentPlayerId()+" typu: "+gameState.getCurrentPlayerType()+
					" wybral kolor: "+chosenColor);
			
			int takenFields = 0;
			int otherTakenFields = gameState.getPreviousPlayerTakenFieldsNumber();
			
			try{
				gameState.makeNextMove(chosenColor);
				takenFields = gameState.getPreviousPlayerTakenFieldsNumber();
				if(chosenColor != null && gameState.getCurrentPlayerType() != PlayerType.HUMAN) {
					makeAiMove = true;
					gameState.makeNextMove(null);
				}
			}
			catch(IncorrectColorException e){
				System.out.println(e.getMessage());
				return;
			}
			if(makeAiMove) {
				otherTakenFields = gameState.getPreviousPlayerTakenFieldsNumber();
			}

			StringBuilder gameInfo=new StringBuilder("*");
			
			if(makeAiMove) {
				gameInfo.append(Integer.toString(gameState.getCurrentPlayerId())+'+');
			}
			else {
				gameInfo.append(Integer.toString(gameState.getPreviousPlayerId())+'+');
			}
			gameInfo.append(Integer.toString(takenFields)+'+');
			gameInfo.append(Integer.toString(gameState.getTurn())+'+');
			gameInfo.append(gameState.getCurrentPlayerType()+"+");
			gameInfo.append(getAvailableColors(gameState));
			gameInfo.append("+");
			gameInfo.append(getMapColors(gameState) + "+");
			gameInfo.append(Integer.toString(otherTakenFields));
			
		
			if (gameState.isGameFinished()) {
				gameState.setGameAction(GameAction.LOAD);
				
				int p1=gameState.getPlayerTakenFieldsNumber(0);
				int p2=gameState.getPlayerTakenFieldsNumber(1);
				int id;
				if(p1==p2)
					id=-1;
				else
					id=gameState.getWinner();
				/*else
					id=(p1>p2)? gameState.getPreviousPlayerId() : gameState.getCurrentPlayerId();*/
				gameInfo = new StringBuilder(id+"+"+p1+"+"+p2+gameInfo);
				
			}
	
			PrintWriter outStream = response.getWriter();
			outStream.write("\""+ gameInfo +"\"");
			outStream.close();
		}
		//"pusta" odpowiedz
		PrintWriter outStream = response.getWriter();
		outStream.write("\""+"empty"+"\"");
		outStream.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Inicjacja restartu gry
		HttpSession session = request.getSession();
		GameState gameState = (GameState) session.getAttribute("gameState");
		if(gameState != null) {
			gameState.setGameAction(GameAction.LOAD);
			gameState.setRestart(true);
		}
		PrintWriter outStream = response.getWriter();
		outStream.write("\" "+ "a" +" \" ");
		outStream.close();
	}
	
	
	private String getAvailableColors(GameState gameState){
		List <Colors> availableColors = gameState.getAvailableColorsForCurrentPlayer();
		StringBuilder colorInfo=new StringBuilder();
		for(Colors color : availableColors){
			colorInfo=colorInfo.append(color.getColorName()+"|");
		}
		return colorInfo.toString();
	}
	private String getMapColors(GameState gameState){
		StringBuilder mapInfo=new StringBuilder();
		List<Colors> table = (List<Colors>) gameState.getCurrentTable();
		int i=0;
		for (Colors color : table) {
			mapInfo=mapInfo.append(color.getColorName());
			if(i++==gameState.getTableWidth()-1){
				mapInfo=mapInfo.append("#");
				i=0;
			}
			else
				mapInfo=mapInfo.append("|");
		}
		return mapInfo.toString();
	}

}
