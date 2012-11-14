package servlets;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GraphExporter
 */
@WebServlet("/GraphExporter")
public class GraphExporter extends HttpServlet {
	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GraphExporter() {
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

		Boolean isImage = false;

		//Get the file type from the URL query
		String type = request.getQueryString().split("=")[1];

		//Set the content type
		if(type.equalsIgnoreCase("xml")) {
			response.setContentType("text/xml");
		}

		else if (type.equalsIgnoreCase("svg")) {
			response.setContentType("image/svg+xml");
		}

		else if (type.equalsIgnoreCase("txt")) {
			response.setContentType("text/plain");
		}

		else if (type.equalsIgnoreCase("png")) {
			response.setContentType("image/png");
			isImage = true;
		}


		//Force the browser to download the file
		String filename = "network." +type ; 
		String value = "attachment; filename = "  +filename;		
		response.setHeader("Content-Disposition", value);
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Transfer-Encoding", "binary");

		//Send the data to the browser
		if (isImage) { //use Image

			//Get the network from the flash
			Image image = getNetworkAsImage(request, response);
			ServletOutputStream output = response.getOutputStream();
			ImageIO.write((RenderedImage) image, "png", output);
			output.close();

		}

		else { //use String

			//Get the network from the flash
			String networkString = getNetworkAsString(request, response);
			ServletOutputStream output = response.getOutputStream();
			output.print(networkString);
			output.close();
		}

	}

	private Image getNetworkAsImage(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		Image content = ImageIO.read(request.getInputStream());
		return content;
	}

	private String getNetworkAsString(HttpServletRequest request, HttpServletResponse response) throws IOException {

		InputStreamReader isr = new InputStreamReader(request.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		StringWriter out = new StringWriter(); 
		String line;
		while ((line = br.readLine()) != null) {
			out.write(line);		
		}
		br.close();
		String content = out.toString();
		return content; 
	}

}
