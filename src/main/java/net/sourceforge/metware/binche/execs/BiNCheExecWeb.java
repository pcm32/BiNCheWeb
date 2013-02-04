/*
 * Copyright (c) 2012, Stephan Beisken. All rights reserved.
 *
 * This file is part of BiNChe.
 *
 * BiNChe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BiNChe is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BiNChe. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.metware.binche.execs;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.metware.binche.BiNChe;
import net.sourceforge.metware.binche.graph.ChEBIGraphPruner;
import net.sourceforge.metware.binche.graph.ChebiEdge;
import net.sourceforge.metware.binche.graph.ChebiGraph;
import net.sourceforge.metware.binche.graph.ChebiVertex;
import net.sourceforge.metware.binche.graph.LinearBranchCollapserPruner;
import net.sourceforge.metware.binche.graph.LowPValueBranchPruner;
import net.sourceforge.metware.binche.graph.MoleculeLeavesPruner;
import net.sourceforge.metware.binche.graph.RootChildrenPruner;
import net.sourceforge.metware.binche.graph.ZeroDegreeVertexPruner;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import BiNGO.BingoParameters;
import BiNGO.ParameterFactory;

/**
 * @author Stephan Beisken, Bhavana Harsha, Janna Hastings
 */
public class BiNCheExecWeb {

    private static final Logger LOGGER = Logger.getLogger(BiNCheExecWeb.class);

    private Properties binCheProps; 
    private Map<String,String> targetNameToOntologyFileMap = new HashMap<String, String>();
    private Map<String,String> targetNameToAnnotationFileMap = new HashMap<String, String>();
    
    public BiNCheExecWeb() {
    	binCheProps = new Properties();
    	try {
    		InputStream in = getClass().getClassLoader().getResourceAsStream("binche_gui.properties");
    		binCheProps.load(in);
    		in.close();
    	} catch (Exception e) {
    		throw new InstantiationError("Unable to load properties file, cannot proceed.");
    	}
    	
    	setupTargetNames();
    	
    }

    //depends on the properties having been initialised successfully. 
    private void setupTargetNames() {
    	int numTargets = Integer.valueOf(binCheProps.getProperty("menu.countTargets"));
    	for (int i=1; i<=numTargets; i++) {
    		String targetType = binCheProps.getProperty("menu.targetType."+i);
    		if (targetType != null) {
    			String ontologyFileName = binCheProps.getProperty("target.ontologyFile."+i);
    			if (ontologyFileName != null) {
    				targetNameToOntologyFileMap.put(targetType, ontologyFileName);
    			}
    			String annotationFileName = binCheProps.getProperty("target.annotationFile."+i);
    			if (annotationFileName != null) {
    				targetNameToAnnotationFileMap.put(targetType, annotationFileName);
    			}
    		}
    	}
    }
    
    public Set<String> getTargetNames() {
    	return targetNameToOntologyFileMap.keySet();
    }
    
    /**
     * Processes the input data which is sent by the web interface, performs enrichment analysis, draws the graph,
     * prunes it to a suitable size and converts the nodes and edges to a json format.
     * This method is intended to be be called by the web interface.
     * @param input
     * @param request
     * @param response
     * @throws IOException
     * @throws URISyntaxException
     */
    public void processData(HashMap<String, String> input, HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {

        LOGGER.log(Level.INFO, "############ Start ############");

        String analysisType = request.getSession().getAttribute("analysisType").toString();
        System.out.println("analysisType =  "+analysisType);
        
        String target = request.getSession().getAttribute("targetType").toString();
        
        String ontologyFileName = targetNameToOntologyFileMap.get(target);
        File ontologyFile = new File(ontologyFileName);
        if (ontologyFile == null || !ontologyFile.exists()) {
        	//problem!
        	throw new FileNotFoundException("Unable to open ontology file: "+ontologyFile.getName());
        }
        System.out.println("Got ontology file: "+ontologyFileName);
        
        String annotationFileName = targetNameToAnnotationFileMap.get(target);
        if (annotationFileName != null) {
        	File annotationFile = new File(annotationFileName);
            if (annotationFile == null || !annotationFile.exists()) {
            	//problem!
            	throw new FileNotFoundException("Unable to open annotation file: "+annotationFile.getName());
            }
        }
        System.out.println("Got annotation file name: "+annotationFileName);
        
        LOGGER.log(Level.INFO, "Setting parameters ...");
        
        //Different parameters for weighted and plain analysis
        BingoParameters parametersChEBIBin = (analysisType.equals("weighted")?
        		ParameterFactory.makeParametersForChEBISaddleSum(ontologyFileName) :
        		ParameterFactory.makeParametersForChEBIBinomialOverRep(ontologyFileName) );
        
        if (annotationFileName != null) {
        	parametersChEBIBin.setAnnotationFile(annotationFileName);
        }
        BiNChe binche = new BiNChe();
        binche.setParameters(parametersChEBIBin);

        LOGGER.log(Level.INFO, "Reading input file ...");
        try {
            binche.loadDesiredElementsForEnrichmentFromInput(input);
        } catch (IOException exception) {
            LOGGER.log(Level.ERROR, "Error reading file: " + exception.getMessage());
            System.exit(1);
        }
        binche.execute();

        ChebiGraph chebiGraph = new ChebiGraph(binche.getPValueMap(), binche.getOntology(), binche.getNodes());

        List<ChEBIGraphPruner> pruners = Arrays.asList(new MoleculeLeavesPruner(), new LowPValueBranchPruner(0.05)
                , new LinearBranchCollapserPruner(), new RootChildrenPruner(3), new ZeroDegreeVertexPruner());
        int originalVertices = chebiGraph.getVertexCount();
        System.out.println("Number of nodes before pruning : " + originalVertices);

        int prunes=0;
        for (ChEBIGraphPruner chEBIGraphPruner : pruners) {
            if (chebiGraph.getVertexCount()>50 && !analysisType.equals("weighted")) { //only prune for plain enrichment
                chEBIGraphPruner.prune(chebiGraph);
                prunes++;
                System.out.println(chEBIGraphPruner.getClass().getCanonicalName());
                System.out.println("Removed vertices : " + (originalVertices - chebiGraph.getVertexCount()));
                originalVertices = chebiGraph.getVertexCount();
            }
        }

        int finalVertices = chebiGraph.getVertexCount();

        System.out.println("Final vertices : " + (finalVertices));

        //Convert the chebi Graph to a JSON Object for display on webapp
        getChebiGraphAsJson(chebiGraph, request, response);

        LOGGER.log(Level.INFO, "############ Stop ############");

        response.setContentType("text/html");

    }


    /**
     * Converts the graph into a list of nodes and a list of edges.
     * @param chebiGraph
     * @param request
     * @param response
     */
    private void getChebiGraphAsJson(ChebiGraph chebiGraph, HttpServletRequest request, HttpServletResponse response) {

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
            if (colorMap.containsKey(vertex.getChebiName())) {
                String color = colorMap.get(vertex.getChebiName()).toString();
                String nodeColor = "color :" +"\"" +color +"\"";
                Integer alpha = vertex.getColor().getAlpha();
                double alphaScaled = scaleAlpha(alpha);
                String nodeAlpha = "alpha :" +"\""+alphaScaled +"\"";
                nodeList.add("{ " +nodeId +" , " +nodeLabel +" , " +nodeColor +" , " +nodeAlpha +" ," +nodePValue +" }");
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
    public static String toHex(int r, int g, int b) {
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g) + toBrowserHexValue(b);
    }

    private static String toBrowserHexValue(int number) {
        StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
        while (builder.length() < 2) {
            builder.append("0");
        }
        return builder.toString().toUpperCase();
    }

}
