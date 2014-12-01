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
		System.out.println(session);
		System.out.println(gameState);
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

			String gameInfo=new String();
			gameInfo=gameInfo.concat(gameState.getCurrentPlayerType()+"+");
			gameInfo=gameInfo.concat(gameState.getTableWidth()+"+"+gameState.getTableHeight()+"+");
			gameInfo=gameInfo.concat(getAvailableColors(gameState));
			gameInfo=gameInfo.concat("+");		
			gameInfo=gameInfo.concat(getMapColors(gameState));
			PrintWriter outStream = response.getWriter();
			outStream.write("\""+gameInfo+"\"");
			outStream.close();
			return;
		}
		if(action == GameAction.PLAY && request.getParameter("size") != null) {
			System.out.println("Continuing game");
			String gameInfo=new String();
			gameInfo=gameInfo.concat(gameState.getCurrentPlayerType()+"+");
			gameInfo=gameInfo.concat(gameState.getTableWidth()+"+"+gameState.getTableHeight()+"+");
			gameInfo=gameInfo.concat(getAvailableColors(gameState));
			gameInfo=gameInfo.concat("+");		
			gameInfo=gameInfo.concat(getMapColors(gameState));
			PrintWriter outStream = response.getWriter();
			outStream.write("\""+gameInfo+"\"");
			outStream.close();
			return;
		}
		if(action==GameAction.PLAY) {
			System.out.println("xax");
			String chosenColor;
			if(gameState.getCurrentPlayerType().equals(PlayerType.AI_MIN_MAX))
				chosenColor=null;
			else
				chosenColor = request.getParameter("color");
			
			System.out.println("Gracz o id: "+gameState.getCurrentPlayerId()+" typu: "+gameState.getCurrentPlayerType()+
					" wybral kolor: "+chosenColor);
			try{
				gameState.makeNextMove(chosenColor);
			}
			catch(IncorrectColorException e){
				System.out.println(e.getMessage());
				return;
			}
			
			String gameInfo=new String("*");
			
			int takenFields = gameState.getPreviousPlayerTakenFieldsNumber();
			
			gameInfo=gameInfo.concat(Integer.toString(gameState.getPreviousPlayerId())+'+');
			gameInfo=gameInfo.concat(Integer.toString(takenFields)+'+');
			gameInfo=gameInfo.concat(Integer.toString(gameState.getTurn())+'+');
			gameInfo=gameInfo.concat(gameState.getCurrentPlayerType()+"+");
			gameInfo=gameInfo.concat(getAvailableColors(gameState));
			gameInfo=gameInfo.concat("+");
			gameInfo=gameInfo.concat(getMapColors(gameState));
			
		
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
				gameInfo=id+"+"+p1+"+"+p2+gameInfo;
				
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
		String colorInfo=new String();
		for(Colors color : availableColors){
			colorInfo=colorInfo.concat(color.getColorName()+"|");
		}
		return colorInfo;
	}
	private String getMapColors(GameState gameState){
		String mapInfo=new String();
		List<Colors> table = (List<Colors>) gameState.getCurrentTable();
		int i=0;
		for (Colors color : table) {
			mapInfo=mapInfo.concat(color.getColorName());
			if(i++==gameState.getTableWidth()-1){
				mapInfo=mapInfo.concat("#");
				i=0;
			}
			else
				mapInfo=mapInfo.concat("|");
		}
		return mapInfo;
	}

}
