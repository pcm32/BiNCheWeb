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
package net.sourceforge.metware.binche.graph.core;

import cytoscape.data.annotation.Ontology;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class providing access to a rooted acyclic minimum spanning tree plus visualisation functionality required to write
 * and display the graph. The graph is tailored to deal with the ChEBI ontology.
 */
public class ChebiGraph {

	private static final Logger LOGGER = Logger.getLogger(ChebiGraph.class);

	private Graph<ChebiVertex, ChebiEdge> graph;
	private Map<Integer, ChebiVertex> vertexMap;
	private Set<String> edgeSet;
	private int vertexId = 0;

	private ColorGradient gradient;

	private Ontology ontology;
	private Map<Integer, Double> pValueMap;

	private Layout<ChebiVertex, ChebiEdge> layout;

	//Added getters and setters to access the elements of ChebiGraph 
	//in order to convert it to a JSON object in BiNCheExec

	public Graph<ChebiVertex, ChebiEdge> getGraph() {
		return graph;
	}


	public void setGraph(Graph<ChebiVertex, ChebiEdge> graph) {
		this.graph = graph;
	}


	public Map<Integer, ChebiVertex> getVertexMap() {
		return vertexMap;
	}


	public void setVertexMap(Map<Integer, ChebiVertex> vertexMap) {
		this.vertexMap = vertexMap;
	}


	public Set<String> getEdgeSet() {
		return edgeSet;
	}


	public void setEdgeSet(Set<String> edgeSet) {
		this.edgeSet = edgeSet;
	}


	public ColorGradient getGradient() {
		return gradient;
	}


	public void setGradient(ColorGradient gradient) {
		this.gradient = gradient;
	}


	public Ontology getOntology() {
		return ontology;
	}


	public void setOntology(Ontology ontology) {
		this.ontology = ontology;
	}


	public Map<Integer, Double> getpValueMap() {
		return pValueMap;
	}


	public void setpValueMap(Map<Integer, Double> pValueMap) {
		this.pValueMap = pValueMap;
	}


	public Layout<ChebiVertex, ChebiEdge> getLayout() {
		return layout;
	}


	public void setLayout(Layout<ChebiVertex, ChebiEdge> layout) {
		this.layout = layout;
	}


	public int getVertexId() {
		return vertexId;
	}


	public void setVertexId(int vertexId) {
		this.vertexId = vertexId;
	}


	/**
	 * Constructs the ChEBI graph.
	 *
	 * @param pValueMap the p values and their corresponding ChEBI ids from the enrichment analysis
	 * @param ontology  the parsed ontology
	 * @param nodes     the set of input ChEBI ids
	 */
	public ChebiGraph(Map<Integer, Double> pValueMap, Ontology ontology, HashSet<String> nodes) {

		this.ontology = ontology;
		//this.pValueMap = pValueMap; //originally this statement before filtering out elements on the basis of p-value 

		//************Addition***************
		Map<Integer, Double> pValueMapTrimmed = new HashMap<Integer, Double>();
		if (pValueMap.size()>20) {
			for (Integer id : pValueMap.keySet()) {
				if (pValueMap.get(id)<0.05) {
					pValueMapTrimmed.put(id, pValueMap.get(id));
				}
			}

			this.pValueMap = pValueMapTrimmed;
		}

		else this.pValueMap = pValueMap;
		//**********Ends********************

		this.gradient = new ColorGradient(pValueMap.values());

		// extract numeral ChEBI ID
		HashSet<String> nodesMod = new HashSet<String>();
		for (String chebiId : nodes) {
			nodesMod.add(chebiId.split(":")[1]);
		}
		nodes = nodesMod;

		//populateGraph(pValueMap, ontology, nodes);
		populateGraphTrimmed(pValueMap, ontology, nodes);
		layoutGraph();
	}


	/**
	 * Creates and populates an undirected sparse multigraph.
	 *
	 * @param pValueMap the p values and their corresponding ChEBI ids from the enrichment analysis
	 * @param ontology  the parsed ontology
	 * @param nodes     the set of input ChEBI ids
	 */
	private void populateGraph(Map<Integer, Double> pValueMap, Ontology ontology, HashSet<String> nodes) {

		graph = new UndirectedOrderedSparseMultigraph<ChebiVertex, ChebiEdge>();

		vertexMap = new HashMap<Integer, ChebiVertex>();
		edgeSet = new HashSet<String>();

		int previousId;
		int currentId;

		for (String node : nodes) {

			int[][] hierarchy = ontology.getAllHierarchyPaths(Integer.parseInt(node));

			for (int row = 0; row < hierarchy.length; row++) {

				previousId = hierarchy[row][hierarchy[row].length - 1];
				addVertex(previousId);

				for (int col = hierarchy[row].length - 2; col >= 0; col--) {

					currentId = hierarchy[row][col];
					addVertex(currentId);
					addEdge(previousId, currentId);

					previousId = currentId;
				}
			}
		}
	}

	//Test to see if removing nodes with high p-value will make the graph significantly smaller 
	//by adding if conditional before addVertex
	@SuppressWarnings("deprecation")
	private void populateGraphTrimmed(Map<Integer, Double> pValueMap, Ontology ontology, HashSet<String> nodes) {

		graph = new UndirectedOrderedSparseMultigraph<ChebiVertex, ChebiEdge>();

		vertexMap = new HashMap<Integer, ChebiVertex>();
		edgeSet = new HashSet<String>();

		int previousId;
		int currentId;
		//		ArrayList<String> uninterestingNodes = new ArrayList<String>(33582, );

		for (String node : nodes) {

			int[][] hierarchy = ontology.getAllHierarchyPaths(Integer.parseInt(node));

			for (int row = 0; row < hierarchy.length; row++) {

				previousId = hierarchy[row][hierarchy[row].length - 1];

				//				addVertex(previousId);

				for (int col = hierarchy[row].length - 2; col >= 0; col--) {

					currentId = hierarchy[row][col];

					//trimming the intermediate non-enriched nodes (length-4 --> 2 parents, length-5 --> 3 parents)
					if (this.pValueMap.containsKey(currentId) || col > hierarchy[row].length-4 ){

						addVertex(previousId);
						addVertex(currentId);
						addEdge(previousId, currentId);
						previousId = currentId;
					}

					else continue;
				}
			}
		}
	}

	/** Adds a vertex to the vertex map if not alrady contained and sets its color depending on the estimated p value
	 *  from the enrichment analysis.
	 *
	 * @param id the id of the vertex to be added
	 */
	private void addVertex(int id) {

		if (!vertexMap.containsKey(id)) {

			vertexMap.put(id, new ChebiVertex(vertexId, "" + id, ontology.getTerm(id).getName()));
			if (pValueMap.containsKey(id)) vertexMap.get(id).setColor(gradient.getGradientColor(pValueMap.get(id)));

			vertexId++;
		}
	}

	/**
	 * Adds an edge plus its two vertices to the graph if not already contained.
	 *
	 * @param previousId first partner vertex id;
	 * @param currentId  second partner vertex id
	 */
	private void addEdge(int previousId, int currentId) {

		ChebiEdge edge = new ChebiEdge(previousId + "-" + currentId, 0d);

		if (!edgeSet.contains(edge.getId())) {

			graph.addEdge(edge, vertexMap.get(previousId), vertexMap.get(currentId));
			edgeSet.add(edge.getId());
		}
	}

	/**
	 * Creates a rooted acyclic tree from the undirected sparse multigraph by calculating its minimum spanning trees.
	 * The resulting forest should ideally contain a single tree since the called ontology hierarchies should be
	 * interconnected.
	 * <p/>
	 * The size of the graph is determined by the values x, y in the tree layout.
	 * <p/>
	 * The tree is rooted to ChEBI:24431 "chemical entity" using custom implementations of the MinimumSpanningForest2
	 * (here, SpanningForest) and PrimSpanningTree (here, SpanningTree) classes.
	 */
	private void layoutGraph() {

		SpanningForest<ChebiVertex, ChebiEdge> prim =
				new SpanningForest<ChebiVertex, ChebiEdge>(graph, new DelegateForest<ChebiVertex, ChebiEdge>(),
						DelegateTree.<ChebiVertex, ChebiEdge>getFactory(), new ConstantTransformer(1.0));

		Forest<ChebiVertex, ChebiEdge> forest = prim.getForest();
		layout = new TreeLayout<ChebiVertex, ChebiEdge>(forest, 80, 80);
		//layout = new SpringLayout<ChebiVertex, ChebiEdge>(graph);

		// re-creates the original graph with the forest node coordinates
		// Layout<ChebiVertex, ChebiEdge> treeLayout = new TreeLayout<ChebiVertex, ChebiEdge>(forest);
		// layout = new StaticLayout<ChebiVertex, ChebiEdge>(graph, treeLayout);
	}

	/**
	 * Gets the visualisation viewer to display the graph.
	 *
	 * @param dimension size of the viewer window
	 * @return the visualisation viewer
	 */
	public VisualizationViewer<ChebiVertex, ChebiEdge> getVisualizationViewer(Dimension dimension) {

		VisualizationViewer<ChebiVertex, ChebiEdge> bvs = new VisualizationViewer<ChebiVertex, ChebiEdge>(layout);
		bvs.setSize(dimension);

		setTransformer(bvs);
		setMouse(bvs);

		return bvs;
	}

	/**
	 * Gets the visualisation server to write the graph.
	 *
	 * @return the visualisation server
	 */
	public VisualizationImageServer<ChebiVertex, ChebiEdge> getVisualisationServer() {

		VisualizationImageServer<ChebiVertex, ChebiEdge> vis =
				new VisualizationImageServer<ChebiVertex, ChebiEdge>(layout, layout.getSize());
		setTransformer(vis);

		return vis;
	}

	/**
	 * Sets all vertex and edge render parameters (transformers).
	 *
	 * @param bvs the visualisation server
	 */
	private void setTransformer(BasicVisualizationServer<ChebiVertex, ChebiEdge> bvs) {

		bvs.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<ChebiVertex>());
		bvs.getRenderContext().setVertexFillPaintTransformer(getVertexTransformer());

		bvs.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<ChebiEdge>());
		bvs.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());

		bvs.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.S);
	}

	/**
	 * Method to return the color transformer of a node, working on its internal color value.
	 *
	 * @return the transformer
	 */
	public Transformer<ChebiVertex, Paint> getVertexTransformer() {

		Transformer<ChebiVertex, Paint> vertexPaint = new Transformer<ChebiVertex, Paint>() {

			@Override
			public Paint transform(ChebiVertex vertex) {

				return vertex.getColor();
			}
		};

		return vertexPaint;
	}

	/**
	 * Adds default mouse functionality to the graph.
	 *
	 * @param bvs the visualisation viewer
	 */
	private void setMouse(VisualizationViewer<ChebiVertex, ChebiEdge> bvs) {

		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
		bvs.setGraphMouse(gm);
	}
}
