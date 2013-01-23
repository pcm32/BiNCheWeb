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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import org.apache.log4j.Priority;

/**
 * @author Stephan Beisken
 */
public class BiNCheExecWeb {

    private static final Logger LOGGER = Logger.getLogger(BiNCheExecWeb.class);

    public static void main(String[] args, HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Starting main method....");
        BiNCheExecWeb bincheexec = new BiNCheExecWeb();
        // I don't know whether this makes sense
//				bincheexec.processData(args, request, response);
    }

    public BiNCheExecWeb() {

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
        Boolean structureEnrichment = false;
        Boolean roleEnrichment = false;

        String ontologyFile = null;

        String target = request.getSession().getAttribute("targetType").toString();
        if (target.equalsIgnoreCase("structure")) {
            ontologyFile = BiNChe.class.getResource("/BiNGO/data/chebiInferred_chemEnt.obo").toURI().toString();
            structureEnrichment = true;
        }
        else if (target.equalsIgnoreCase("role")) {
            ontologyFile = BiNChe.class.getResource("/BiNGO/data/chebiInferred_roles.obo").toURI().toString();
            roleEnrichment = true;
        }
        else if(target.equalsIgnoreCase("structure and role")) {
            ontologyFile = BiNChe.class.getResource("/BiNGO/data/chebiInferred_chemEnt_roles.obo").toURI().toString();
            structureEnrichment= true;
        }

        /**
         * Check whether we are doing normal enrichment analysis or weighted enrichment
         */
        String analysisType = request.getSession().getAttribute("analysisType").toString();
        BingoParameters parametersChEBIBin;
        if(analysisType.equalsIgnoreCase("plain")) {
            LOGGER.log(Level.INFO, "Setting default parameters for plain enrichment...");
            parametersChEBIBin = ParameterFactory.makeParametersForChEBIBinomialOverRep(ontologyFile);
        } else {
            LOGGER.log(Level.INFO, "Setting default parameters for weighted enrichment...");
            parametersChEBIBin = ParameterFactory.makeParametersForWeightedAnalysis(ontologyFile);
        }
        
        

        //Set annotation file separately from ontology file
        // This does not make sense for the case of "structure and role", where only the structure annotations
        // would be used.
        if (structureEnrichment) {
            parametersChEBIBin.setAnnotationFile(BiNChe.class.getResource("/BiNGO/data/ontology-annotations-CHEM.anno").toURI().toString());
        }
        if (roleEnrichment) {
            parametersChEBIBin.setAnnotationFile(BiNChe.class.getResource("/BiNGO/data/ontology-annotations-ROLE.anno").toURI().toString());
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

        /**
         * We only add pruners for the normal enrichment analysis
         * 
         * We need to make a distinction between weighted enrichment analysis for functional analysis
         * and for fragment analysis.
         */
        List<ChEBIGraphPruner> pruners = new ArrayList<ChEBIGraphPruner>();
        if(!parametersChEBIBin.getTest().equalsIgnoreCase(BingoAlgorithm.SADDLESUM)) {
            pruners.addAll(Arrays.asList(new MoleculeLeavesPruner(), new LowPValueBranchPruner(0.05)
                , new LinearBranchCollapserPruner(), new RootChildrenPruner(3), new ZeroDegreeVertexPruner()));
        }
                
        int originalVertices = chebiGraph.getVertexCount();
        System.out.println("Number of nodes before pruning : " + originalVertices);

        int prunes=0;
        for (ChEBIGraphPruner chEBIGraphPruner : pruners) {
            if (chebiGraph.getVertexCount()>50) {
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
        JSONChEBIGraphConverter converter = new JSONChEBIGraphConverter();
        converter.setChebiGraphAsJSON(chebiGraph, request);

        LOGGER.log(Level.INFO, "############ Stop ############");

        response.setContentType("text/html");

    }




    /**
     * Method to set BiNGOParameters.
     * @param ontologyFile
     * @return
     */
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
