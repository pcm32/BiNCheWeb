package servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ValidateInput
 */
@WebServlet("/ValidateInput")
public class ValidateInput extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String error = null;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ValidateInput() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String string = request.getParameter("input").trim();
		String[] input = string.split("\\s+");
		int inputSize = input.length;
		request.setAttribute("inputSize", inputSize); //for testing filter
		String type = request.getParameter("analysisType");
		
		if (type.equalsIgnoreCase("weighted")) {
			Map <String, String> inputMap = new HashMap<String, String>();
			List<String> errors = new ArrayList<String>();

			//Convert string array to a hashmap of ids and weights
			for (int i=0; i<input.length; i+=2) {
				inputMap.put(input[i], input[i+1]);
			}

			//Validate weights
			for (String id : inputMap.keySet()) {
				String weights = inputMap.get(id);
				Double weight = Double.valueOf(weights);
				
				if (weight>0 && weight<1) continue;
				else {
					errors.add(id);
				}
			}

			if (errors.isEmpty()) {
				request.setAttribute("inputMap", inputMap);
				//response.sendRedirect("pages/ResultPage.jsp");
				request.getRequestDispatcher("pages/ResultJSON.jsp").forward(request, response);
			}
			else {
				response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "The weight has to be a number between 0 and 1!");

			}
		}
		else if (type.equalsIgnoreCase("plain")) {
			Map<String,String> dummyMap = new HashMap<String,String>();
			for(String id : input) {
				dummyMap.put(id, "1");
			}
			request.setAttribute("inputMap", dummyMap);
			request.getRequestDispatcher("pages/ResultJSON.jsp").forward(request, response);
		}
	}

}
