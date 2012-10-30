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

import BiNGO.BingoParameters;
import BiNGO.ParameterFactory;
import BiNGO.methods.BingoAlgorithm;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import net.sourceforge.metware.binche.BiNChe;
import net.sourceforge.metware.binche.graph.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import sun.jkernel.Bundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

/**
 * @author Stephan Beisken
 */
public class BiNCheExecWeb {

	private static final Logger LOGGER = Logger.getLogger(BiNCheExecWeb.class);

	public static void main(String[] args, HttpServletRequest request, HttpServletResponse response) throws IOException {

		System.out.println("Starting main method....");
//		BiNCheExecWeb bincheexec = new BiNCheExecWeb();
		//		bincheexec.generateImage(args, request, response);
	}

	public BiNCheExecWeb() {

	}

	public RenderedImage generateImage(HashMap<String, String> input, HttpServletRequest request, HttpServletResponse response) throws IOException {

		LOGGER.log(Level.INFO, "############ Start ############");

		String ontologyFile = "/home/bhavana/workspace/BiNChe_JSP/BiNCheJSP/src/main/resources/data/chebi_role_only.obo";

		LOGGER.log(Level.INFO, "Setting default parameters ...");
		BingoParameters parametersSaddle = getDefaultParameters(ontologyFile);

		BiNChe binche = new BiNChe();
		binche.setParameters(parametersSaddle);

		LOGGER.log(Level.INFO, "Reading input file ...");
		try {
			binche.loadDesiredElementsForEnrichmentFromInput(input);
		} catch (IOException exception) {
			LOGGER.log(Level.ERROR, "Error reading file: " + exception.getMessage());
			System.exit(1);
		}
		binche.execute();

		ChebiGraph chebiGraph =
				new ChebiGraph(binche.getPValueMap(), binche.getOntology(), binche.getNodes());

		LOGGER.log(Level.INFO, "Displaying out graph ...");

		VisualizationImageServer<ChebiVertex, ChebiEdge> imageServer = chebiGraph.getVisualisationServer();
		Image image = imageServer.getImage(new Point.Double(0, 0), new Dimension(1800, 1440));
		RenderedImage bfImage = toBufferedImage(image);

		LOGGER.log(Level.INFO, "############ Stop ############");

		HttpSession session = request.getSession();
		response.setContentType("text/html");
		session.setAttribute("chebiGraph", bfImage);

		return bfImage;
	}

	public void generateJson(HashMap<String, String> input, HttpServletRequest request, HttpServletResponse response) throws IOException {

		LOGGER.log(Level.INFO, "############ Start ############");

		//Assign appropriate ontology file depending on target selected by user
//        String ontologyFile = BiNCheExecWeb.class.getResource("/data/chebi_clean.obo").getFile();

		String ontologyFile = null;

		String target = request.getParameter("targetType");
		if (target.equalsIgnoreCase("structure")) {
			ontologyFile = BiNCheExecWeb.class.getResource("/data/chebi_structure_only.obo").getFile();
		}
		else if (target.equalsIgnoreCase("role")) {
			ontologyFile = BiNCheExecWeb.class.getResource("/data/chebi_role_only.obo.obo").getFile();
		}
		else if(target.equalsIgnoreCase("both")) {
			ontologyFile = BiNCheExecWeb.class.getResource("/data/chebi_role_and_structure.obo.obo").getFile();
		}

		LOGGER.log(Level.INFO, "Setting default parameters ...");
//		BingoParameters parametersSaddle = getDefaultParameters(ontologyFile);
        BingoParameters parametersChEBIBin = ParameterFactory.makeParametersForChEBIBinomialOverRep(ontologyFile);

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

		ChebiGraph chebiGraph =
				new ChebiGraph(binche.getPValueMap(), binche.getOntology(), binche.getNodes());

		List<ChEBIGraphPruner> pruners = Arrays.asList(new MoleculeLeavesPruner(), new LowPValueBranchPruner(0.05)
		, new LinearBranchCollapserPruner(), new RootChildrenPruner(3), new ZeroDegreeVertexPruner());
		int originalVertices = chebiGraph.getVertexCount();
		System.out.println("Number of nodes before pruning : " + originalVertices);

		int prunes=0;
		for (ChEBIGraphPruner chEBIGraphPruner : pruners) {
			chEBIGraphPruner.prune(chebiGraph);
			prunes++;
			System.out.println(chEBIGraphPruner.getClass().getCanonicalName());
			System.out.println("Removed vertices : " + (originalVertices - chebiGraph.getVertexCount()));
			originalVertices = chebiGraph.getVertexCount();
			System.out.println("Writing out graph ...");
		}
		
		int finalVertices = chebiGraph.getVertexCount();

		System.out.println("Final vertices : " + (finalVertices));


		//Convert the chebi Graph to a JSON Object for display on webapp
		getJsonObjectFromGraph(chebiGraph, request, response);

		LOGGER.log(Level.INFO, "############ Stop ############");

		response.setContentType("text/html");

	}

	private void getJsonObjectFromGraph(ChebiGraph chebiGraph, HttpServletRequest request, HttpServletResponse response) {

		//layout
		//Layout<ChebiVertex, ChebiEdge> layout = chebiGraph.getLayout();
		//Graph<ChebiVertex, ChebiEdge> layoutGraph = layout.getGraph();

		//Colors
		//Collection<ChebiVertex> graphVertices = layoutGraph.getVertices();
		Map<String, String> colorMap = new HashMap<String, String>();
		for (ChebiVertex vertex : chebiGraph.getVertices()) {
			Color rgbColor = vertex.getColor();
			String hexColor = toHex(rgbColor.getRed(), rgbColor.getGreen(), rgbColor.getBlue());
			colorMap.put(vertex.getChebiName(), hexColor);
		}		

		//NODES
		Map<Integer, ChebiVertex> vertexmap = new HashMap<Integer, ChebiVertex>();
		List <String> nodeList = new ArrayList<String>();
		for (ChebiVertex vertex : chebiGraph.getVertices()) {
			String nodeId = "id :" +"\"" +vertex.getChebiId() +"\"";
			String nodeLabel = "label :" +"\"" +vertex.getChebiName() +"\"";
			if (colorMap.containsKey(vertex.getChebiName())) {
				String color = colorMap.get(vertex.getChebiName()).toString();
				String nodeColor = "color :" +"\"" +color +"\"";
				nodeList.add("{ " +nodeId +" , " +nodeLabel +" , " +nodeColor +" }"); 
			}
			else nodeList.add("{ " +nodeId +" , " +nodeLabel +" }"); 
		}

		//NODES - Older version which requires getVertexMap and getLayout methods in ChebiGraph 
		//		Map<Integer, ChebiVertex> vertexmap = chebiGraph.getVertexMap();
		//		List <String> nodeList = new ArrayList<String>();
		//		for (Integer key : vertexmap.keySet()) {
		//			String chebiName = vertexmap.get(key).getChebiName();
		//			String nodeId = "id :" +"\"" +Integer.toString(key) +"\"";
		//			String nodeLabel = "label :" +"\"" +chebiName +"\"";			
		//			if (colorMap.containsKey(chebiName)) {
		//				String color = colorMap.get(chebiName).toString();
		//				String nodeColor = "color :" +"\"" +color +"\"";
		//				nodeList.add("{ " +nodeId +" , " +nodeLabel +" , " +nodeColor +" }"); 
		//			}
		//			else nodeList.add("{ " +nodeId +" , " +nodeLabel +" }"); 
		//		}

		int outputSize = nodeList.size();
		request.setAttribute("outputSize", outputSize); //to display on website for testing filter

		//Move 24431=chemical entity element to the top of the list,
		//because cytoscape web layout sets the first node it receives as the root of the tree.
		//If it is already in the first position, the list remains unchanged

		//		for(String element : nodeList){
		//			if (element.indexOf("24431")!= -1) {
		//				Collections.swap(nodeList, nodeList.indexOf(element), 0);
		//				break;
		//			}
		//		}

		//EDGES
		List <String> edgeList = new ArrayList<String>();
		//get edges from layout.getGraph instead of chebiGraph.getGraph, because layout.graph has trimmed edges
		//Solved the problem of multiple parents for nodes
		//Collection<ChebiEdge> edgeSet = layoutGraph.getEdges(); 

		Collection<ChebiEdge> edgeSet = chebiGraph.getEdges();

		for (ChebiEdge edge : edgeSet) {
			//swapped vertices to reverse direction of edges
			String vertexOne = edge.getId().split("-")[1];
			String vertexTwo = edge.getId().split("-")[0];
			vertexOne = "source : " +"\"" +vertexOne +"\"";
			vertexTwo = "target : " +"\"" +vertexTwo +"\"";		
			edgeList.add("{ " +vertexTwo + " , " +vertexOne +" }");
		}

		HttpSession session = request.getSession();
		session.setAttribute("nodeList", nodeList);
		session.setAttribute("edgeList", edgeList);
		request.setAttribute("outputSize", outputSize);

	}

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

	private static BufferedImage toBufferedImage(Image src) {

		int w = src.getWidth(null);
		int h = src.getHeight(null);
		int type = BufferedImage.TYPE_INT_ARGB;
		BufferedImage dest = new BufferedImage(w, h, type);
		Graphics2D g2 = dest.createGraphics();
		g2.fillRect(0, 0, w, h);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(src, 0, 0, null);
		g2.dispose();
		return dest;
	}

	public BingoParameters getDefaultParameters(String ontologyFile) {

		BingoParameters parametersSaddle = new BingoParameters();

		parametersSaddle.setTest(BingoAlgorithm.SADDLESUM);
		parametersSaddle.setCorrection(BingoAlgorithm.NONE);
		parametersSaddle.setOntologyFile(ontologyFile);
		parametersSaddle.setOntology_default(false);
		parametersSaddle.setNameSpace("chebi_ontology");
		parametersSaddle.setOverOrUnder("Overrepresentation");
		parametersSaddle.setSignificance(new BigDecimal(0.05));
		parametersSaddle.setCategory(BingoAlgorithm.CATEGORY_CORRECTION);
		parametersSaddle.setReferenceSet(BingoAlgorithm.GENOME);
		parametersSaddle.setAllNodes(null);
		parametersSaddle.setSelectedNodes(null);

		return parametersSaddle;
	}
}
