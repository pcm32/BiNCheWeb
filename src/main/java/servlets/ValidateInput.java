package servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ValidateInput
 */
@WebServlet("/ValidateInput")
public class ValidateInput extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String error = null;
	private static final String TYPE_FRAGMENT = "fragment";
	private static final String TYPE_PLAIN = "plain";
	private static final String TYPE_WEIGHT = "weighted";


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

		//Process input ids
		String rawInput = request.getParameter("input").trim();
		String[] input = null;
		error = null;
		//Check if values are separated by comma or space
		if (rawInput.split(",").length > 1) {
			input = rawInput.split(",");
		} else {
			input = rawInput.split("\\s+");
		}

		//Get type and target of enrichment
        HttpSession session = request.getSession();
		session.setAttribute("query", rawInput.replace("\r\n"," - "));

		String type = request.getParameter("analysisType");
        session.setAttribute("analysisType", type);

        String target = request.getParameter("targetType");
        session.setAttribute("targetType", target);

		if (type.equalsIgnoreCase(TYPE_WEIGHT) || type.equalsIgnoreCase(TYPE_FRAGMENT)) {

			if (idAndWeight(input[0], input[1])) {
				Map<String, String> inputMap = new HashMap<String, String>();

				//Convert string array to a hashmap of ids and weights
				for (int i = 0; i < input.length; i += 2) {
					inputMap.put(input[i], input[i + 1]);
				}

				//Validate weights
				for (String id : inputMap.keySet()) {
					String weights = inputMap.get(id);

					try {
						Double weight = Double.valueOf(weights);
						if (weight >= 0.0 && weight <= 1.0) continue;
						else {
							error = "Please Check your input you have selected xxxx but the weight is not between number between 0 and 1.";
							break;
						}
					} catch (Exception e) {
						error = "Please Check your input you have selected xxxx but the weight is not between number between 0 and 1.";
					}
				}

				if (error == null) {
					session.setAttribute("inputMap", inputMap);
				}

			} else {
				error = "Please Check your input you have selected xxxx but there is no weight.";
			}

		} else if (type.equalsIgnoreCase(TYPE_PLAIN)) {
			Map<String, String> dummyMap = new HashMap<String, String>();
			for (String id : input) {
				if(!id.contains("CHEBI:"))  {
					error =  "Please Check your input you have selected xxxx but there are weights.";
				}
				dummyMap.put(id, "1");
			}
			session.setAttribute("inputMap", dummyMap);
		}

		//Redirect to the result page if there is no error
		if (error == null) {
			session.removeAttribute("error");
			response.sendRedirect("pages/Result.jsp");
		} else {
			session.setAttribute("error", error);
			response.sendRedirect("pages/index.jsp");
		}
	}

	public static boolean idAndWeight(String chebiId, String weight) {
		return (chebiId.contains("CHEBI:") && !weight.contains("CHEBI:"));
	}

}
