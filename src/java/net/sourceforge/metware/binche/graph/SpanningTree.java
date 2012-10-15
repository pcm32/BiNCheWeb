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

import edu.uci.ics.jung.algorithms.shortestpath.PrimMinimumSpanningTree;
import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

public class SpanningTree<ChebiVertex, ChebiEdge> extends PrimMinimumSpanningTree<ChebiVertex, ChebiEdge> {

    /**
     * Creates an instance which generates a minimum spanning tree using the input edge weights.
     */
    public SpanningTree(Factory<? extends Graph<ChebiVertex, ChebiEdge>> factory,
                        Transformer<ChebiEdge, Double> weights) {

        super(factory, weights);
    }

    @Override
    protected ChebiVertex findRoot(Graph<ChebiVertex, ChebiEdge> graph) {

        for (ChebiVertex v : graph.getVertices()) {
            if (v.toString().equals("chemical entity")) {
                return v;
            }
        }
        // if there is no obvious root, pick any vertex
        if (graph.getVertexCount() > 0) {
            return graph.getVertices().iterator().next();
        }
        // this graph has no vertices
        return null;
    }
}
