/*
 * Copyright (c) 2003 The Regents of the University of California. All Rights Reserved. Permission to use, copy,
 * modify, and distribute this software and its documentation for educational, research and non-profit purposes,
 * without fee, and without a written agreement is hereby granted, provided that the above copyright notice, this
 * paragraph and the following two paragraphs appear in all copies. This software program and documentation are
 * copyrighted by The Regents of the University of California ("The University of California").
 *
 * Modified by Stephan Beisken, European Bioinformatics Institute
 *
 * THE SOFTWARE PROGRAM AND DOCUMENTATION ARE SUPPLIED "AS IS," WITHOUT ANY ACCOMPANYING SERVICES FROM THE UNIVERSITY
 * OF CALFORNIA. FURTHERMORE, THE UNIVERSITY OF CALIFORNIA DOES NOT WARRANT THAT THE OPERATION OF THE PROGRAM WILL BE
 * UNINTERRUPTED OR ERROR-FREE. THE END-USER UNDERSTANDS THAT THE PROGRAM WAS DEVELOPED FOR RESEARCH PURPOSES AND IS
 * ADVISED NOT TO RELY EXCLUSIVELY ON THE PROGRAM FOR ANY REASON.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
  * EVEN IF THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. THE UNIVERSITY OF
  * CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
  * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND
  * THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
  * OR MODIFICATIONS.
*/
package net.sourceforge.metware.binche.graph;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Tree;
import edu.uci.ics.jung.graph.util.TreeUtils;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import java.util.Collection;
import java.util.Set;

/**
 * For the input Graph, creates a MinimumSpanningTree
 * using a variation of Prim's algorithm.
 *
 * @param <ChebiVertex>
 * @param <ChebiEdge>
 * @author Tom Nelson - tomnelson@dev.java.net
 */
@SuppressWarnings("unchecked")
public class SpanningForest<ChebiVertex, ChebiEdge> {

    protected Graph<ChebiVertex, ChebiEdge> graph;
    protected Forest<ChebiVertex, ChebiEdge> forest;
    protected Transformer<ChebiEdge, Double> weights =
            (Transformer<ChebiEdge, Double>) new ConstantTransformer<Double>(1.0);

    /**
     * create a Forest from the supplied Graph and supplied Factory, which
     * is used to create a new, empty Forest. If non-null, the supplied root
     * will be used as the root of the tree/forest. If the supplied root is
     * null, or not present in the Graph, then an arbitary Graph vertex
     * will be selected as the root.
     * If the Minimum Spanning Tree does not include all vertices of the
     * Graph, then a leftover vertex is selected as a root, and another
     * tree is created
     *
     * @param graph
     * @param factory
     * @param weights
     */
    public SpanningForest(Graph<ChebiVertex, ChebiEdge> graph, Factory<Forest<ChebiVertex, ChebiEdge>> factory,
                          Factory<? extends Graph<ChebiVertex, ChebiEdge>> treeFactory,
                          Transformer<ChebiEdge, Double> weights) {

        this(graph, factory.create(), treeFactory, weights);
    }

    /**
     * create a forest from the supplied graph, populating the
     * supplied Forest, which must be empty.
     * If the supplied root is null, or not present in the Graph,
     * then an arbitary Graph vertex will be selected as the root.
     * If the Minimum Spanning Tree does not include all vertices of the
     * Graph, then a leftover vertex is selected as a root, and another
     * tree is created
     *
     * @param graph   the Graph to find MST in
     * @param forest  the Forest to populate. Must be empty
     * @param weights edge weights, may be null
     */
    public SpanningForest(Graph<ChebiVertex, ChebiEdge> graph, Forest<ChebiVertex, ChebiEdge> forest,
                          Factory<? extends Graph<ChebiVertex, ChebiEdge>> treeFactory,
                          Transformer<ChebiEdge, Double> weights) {

        if (forest.getVertexCount() != 0) {
            throw new IllegalArgumentException("Supplied Forest must be empty");
        }
        this.graph = graph;
        this.forest = forest;
        if (weights != null) {
            this.weights = weights;
        }

        WeakComponentClusterer<ChebiVertex, ChebiEdge> wcc = new WeakComponentClusterer<ChebiVertex, ChebiEdge>();
        Set<Set<ChebiVertex>> component_vertices = wcc.transform(graph);
        Collection<Graph<ChebiVertex, ChebiEdge>> components =
                FilterUtils.createAllInducedSubgraphs(component_vertices, graph);

        for (Graph<ChebiVertex, ChebiEdge> component : components) {
            SpanningTree<ChebiVertex, ChebiEdge> mst =
                    new SpanningTree<ChebiVertex, ChebiEdge>(treeFactory, this.weights);
            Graph<ChebiVertex, ChebiEdge> subTree = mst.transform(component);
            if (subTree instanceof Tree) {
                TreeUtils.addSubTree(forest, (Tree<ChebiVertex, ChebiEdge>) subTree, null, null);
            }
        }
    }

    /**
     * Returns the generated forest.
     */
    public Forest<ChebiVertex, ChebiEdge> getForest() {

        return forest;
    }
}
