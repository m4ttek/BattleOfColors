package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PlayerMoveServlet
 * @author Michał Pluta
 */
@WebServlet(description = "Servlet called when a player makes a move", urlPatterns = { "/PlayerMoveServlet" })
public class PlayerMoveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Received x data is " + request.getParameter("x"));
		response.setContentType("application/json");
		PrintWriter outStream = response.getWriter();
		System.out.println("Sending color data");
		outStream.write("[ \"#FF0000\", \"#00FF00\", \"#0000FF\", \"#444444\" ]");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
