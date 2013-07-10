package servlets;

import net.sourceforge.metware.binche.execs.TableWriter;
import net.sourceforge.metware.binche.graph.ChebiGraph;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Produces a table of enriched categories, based on the chebi graph stored in the session.
 *
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
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment;filename=enrichmentTable.txt");

        TableWriter writer = new TableWriter(response.getOutputStream());

        writer.write((ChebiGraph)request.getSession().getAttribute("chebiGraph"));
    }
}
