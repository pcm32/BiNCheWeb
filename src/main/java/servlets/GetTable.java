package servlets;

import encrypt.Encrypter;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 8/7/13
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
public class GetTable extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(GetTable.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String encrypted = (String)request.getParameter("p");
        String fileName = (String)request.getParameter("fn");
        Encrypter encrypter = new Encrypter();
        String path = encrypter.decrypt(encrypted);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment;filename="+fileName+".txt");

        LOGGER.info("Reading from "+path);

        InputStream in = new FileInputStream(path);
        ServletOutputStream out = response.getOutputStream();

        byte[] outputByte = new byte[4096];
        //copy binary contect to output stream
        while(in.read(outputByte, 0, 4096) != -1)
        {
            out.write(outputByte, 0, 4096);
        }
        in.close();
        out.flush();
        out.close();

    }
}
