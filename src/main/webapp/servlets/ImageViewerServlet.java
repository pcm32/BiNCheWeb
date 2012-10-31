package servlets;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer.OutsidePositioner;


/**
 * Servlet implementation class ImageViewer
 */
@WebServlet("/ImageViewer")
public class ImageViewerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageViewerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// get the chart from session 
		HttpSession session = request.getSession(); 
		BufferedImage bufImage = (BufferedImage) session.getAttribute("chebiGraph"); 
		ServletOutputStream outStream = response.getOutputStream();

		//write the image to a byte array so it can be written to the output stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bufImage, "png", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();

		// set the content type so the browser can see this as a picture 
		response.setContentType("image/png"); 

		// send the picture 
		outStream.write(imageInByte);
		outStream.flush();
		outStream.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		doGet(request, response); 	

	}

}
