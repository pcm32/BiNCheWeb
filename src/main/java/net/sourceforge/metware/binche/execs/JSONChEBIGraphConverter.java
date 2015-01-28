/**
 * JSONChEBIGraphConverter.java
 *
 * 2012.12.14
 *
 * This file is part of the CheMet library
 * 
 * The CheMet library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * CheMet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with CheMet.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.metware.binche.execs;


import java.awt.Color;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sourceforge.metware.binche.graph.ChebiEdge;
import net.sourceforge.metware.binche.graph.ChebiGraph;
import net.sourceforge.metware.binche.graph.ChebiVertex;
import org.apache.log4j.Logger;

/**
 * @name    JSONChEBIGraphConverter
 * @date    2012.12.14
 * @version $Rev$ : Last Changed $Date$
 * @author  pmoreno
 * @author  $Author$ (this version)
 * @brief   ...class description...
 *
 */
public class JSONChEBIGraphConverter {

    private static final Logger LOGGER = Logger.getLogger( JSONChEBIGraphConverter.class );


    /**
     * Converts the graph into a list of nodes and a list of edges.
     * This shouldn't be part of this object!!!
     * 
     * @param chebiGraph
     * @param request
     */
    public void setChebiGraphAsJSON(ChebiGraph chebiGraph, HttpServletRequest request) {

        //Convert vertex colours from RGB to hexadecimal for the web-app
        Map<String, String> colorMap = new HashMap<String, String>();
        for (ChebiVertex vertex : chebiGraph.getVertices()) {
            Color rgbColor = vertex.getColor();
            String hexColor = toHex(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
            colorMap.put(vertex.getChebiName(), hexColor);
        }

        //NODES
        List <String> nodeList = getNodesForDisplay(chebiGraph, colorMap);


        //EDGES
        List <String> edgeList = getEdgesForDisplay(chebiGraph);

        HttpSession session = request.getSession();
        session.setAttribute("nodeList", nodeList);
        session.setAttribute("edgeList", edgeList);
    }

    /**
     * Converts the list of edges from ChebiGraph object to a list in json format.
     * @param chebiGraph
     * @return
     */
    private List<String> getEdgesForDisplay(ChebiGraph chebiGraph) {
        List<String> edgeList = new ArrayList<String>();
        Collection<ChebiEdge> edgeSet = chebiGraph.getEdges();
        for (ChebiEdge edge : edgeSet) {
            //swapped vertices to reverse direction of edges
            String vertexOne = edge.getId().split("-")[0];
            String vertexTwo = edge.getId().split("-")[1];
            vertexOne = "source :" +"\"" +vertexOne +"\"";
            vertexTwo = "target :" +"\"" +vertexTwo +"\"";
            edgeList.add("{ " + vertexTwo + " , " + vertexOne + " }");
        }

        return edgeList;
    }

    /**
     * Converts the list of nodes from ChebiGraph object and returns the nodes and their properties
     * (colour, alpha which indicates opacity, label, id) in json format
     * @param chebiGraph
     * @param colorMap
     * @return
     */
    private List getNodesForDisplay (ChebiGraph chebiGraph, Map<String, String> colorMap) {
        List nodeList = new ArrayList();
        for (ChebiVertex vertex : chebiGraph.getVertices()) {
            String nodeId = "id :" +"\"" +vertex.getChebiId() +"\"";
            String nodeLabel = "label :" +"\"" +vertex.getChebiName() +"\"";
            String nodePValue = "pValue :" +"\"" +vertex.getpValue() +"\"";
            String nodeCorrPValue = "corrPValue :" +"\"" +vertex.getCorrPValue() +"\"";
            String nodePropOfSample = "propOfSample :"+"\""+vertex.getSamplePercentage()+"\"";
            String nodeFold = "fold :"+"\""+vertex.getFoldOfEnrichment()+"\"";
            if (colorMap.containsKey(vertex.getChebiName())) {
                String color = colorMap.get(vertex.getChebiName()).toString();
                String nodeColor = "color :" +"\"" +color +"\"";
                Integer alpha = vertex.getColor().getAlpha();
                double alphaScaled = scaleAlpha(alpha);
                String nodeAlpha = "alpha :" +"\""+alphaScaled +"\"";
                nodeList.add("{ " +nodeId +" , " +nodeLabel +" , " +nodeColor +" , " +nodeAlpha +" ," +nodePValue +" ," +nodeCorrPValue+" , " +nodePropOfSample+" , " +nodeFold +" }");
            }
            else nodeList.add("{ " +nodeId +" , " +nodeLabel +" ," +nodePValue +" }");

        }

        return nodeList;
    }

    /**
     * Scales alpha from a 0-255 range to 0-1.0 so it can be passed to CytoscapeWeb for colouring the nodes
     * @param alpha
     * @return
     */
    private double scaleAlpha(Integer alpha) {
        double al = alpha * (1.0/255.0);
        return al;
    }

    /**
     * Returns an RGB colour in a hexadecimal format.
     * @param r
     * @param g
     * @param b
     * @return
     */
    public String toHex(int r, int g, int b) {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    private String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }
}
