package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet obsługujący żądania do modułu statystycznego.
 * @author Mateusz Kamiński
 */
@WebServlet(description = "Servlet called when a user wants to calculate statistcs about game.", 
	urlPatterns = { "/StatisticsServlet" })
public class StatisticsServlet extends HttpServlet{

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 6546005031360199757L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter outStream = response.getWriter();
		outStream.write("\""+"as"+"\"");
		outStream.close();
	}
}
