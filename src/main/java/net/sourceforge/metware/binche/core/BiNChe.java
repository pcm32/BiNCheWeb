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
package net.sourceforge.metware.binche.core;

import BiNGO.BingoParameters;
import BiNGO.interfaces.CalculateCorrectionTask;
import BiNGO.interfaces.CalculateTestTask;
import BiNGO.methods.BingoAlgorithm;
import BiNGO.methods.saddlesum.SaddleSumTestCalculate;
import BiNGO.parser.AnnotationParser;
import BiNGO.parser.ChEBIAnnotationParser;
import cytoscape.data.annotation.Ontology;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class BiNChe {

    private static final Logger LOGGER = Logger.getLogger(BiNChe.class);

    private BingoParameters params;
    private BingoAlgorithm algorithm;
    private final String NONE = BingoAlgorithm.NONE;
    private HashMap<Integer, String> testMap;
    private HashMap<String, String> correctionMap = null;
    private HashMap<Integer, String> mapSmallX = null;
    private HashMap<Integer, String> mapSmallN = null;
    private HashMap<Integer, String> mapBigX = null;
    private HashMap<Integer, String> mapBigN = null;
    private HashMap<String, HashSet<String>> classifiedEntities = null;

    private Map<Integer, Double> pValueMap;

    public void execute() {

        LOGGER.log(Level.INFO, "Parsing annotation ...");

        AnnotationParser annParser = new ChEBIAnnotationParser(params, new HashSet<String>());
        annParser.run();

        if (annParser.getStatus()) {
            params.setAnnotation(annParser.getAnnotation());
            params.setOntology(annParser.getOntology());
            params.setAlias(annParser.getAlias());
            this.setAllNodes(); // this sets all nodes in the params.

            if (annParser.getOrphans()) {
                System.err.println("WARNING : Some category labels in the annotation file" + "\n" +
                        "are not defined in the ontology. Please check the compatibility of" + "\n" +
                        "these files. For now, these labels will be ignored and calculations" + "\n" +
                        "will proceed. " + annParser.getSynHashSize());
            }
            //only way to set status true is to pass annotation parse step
            params.setStatus(true);
        } else {
            params.setStatus(false);
        }

        algorithm = new BingoAlgorithm(params);
        LOGGER.log(Level.INFO, "Calculating distribution ...");
        CalculateTestTask test = algorithm.calculate_distribution();
        test.run();
        testMap = test.getTestMap();

        LOGGER.log(Level.INFO, "Calculating corrections ...");
        CalculateCorrectionTask correction = algorithm.calculate_corrections(testMap);

        if ((correction != null) && (!params.getTest().equals(NONE))) {
            correction.run();
            correctionMap = correction.getCorrectionMap();
        }

        // these hashMaps contain the results, where the Keys are the different categories (ie. a ChEBI entry or a
        // GeneOntology element). These results are after the test we then need to retrieve the corrections from the
        // correction object.
        mapSmallX = test.getMapSmallX();
        mapSmallN = test.getMapSmallN();
        mapBigX = test.getMapBigX();
        mapBigN = test.getMapBigN();

        LOGGER.log(Level.INFO, "Computing elements ...");
        this.computeElementsPerCategory();

        pValueMap = ((SaddleSumTestCalculate) test).getPValueMap();
    }

    /* **********************************
    START - custom methods
    ************************************* */

    public Map<Integer, Double> getPValueMap() {

        return pValueMap;
    }

    public HashMap<String, HashSet<String>> getClassifiedEntities() {

        return classifiedEntities;
    }

    public Ontology getOntology() {

        return params.getOntology();
    }

    public HashSet<String> getNodes() {

        return params.getSelectedNodes();
    }

    /* **********************************
        END - custom methods
    ************************************* */


    public Double getPValueForCategory(Integer categoryID) {

        return Double.parseDouble(testMap.get(categoryID));
    }

    public HashSet<String> getElementsInCategory(Integer categoryID) {

        return this.classifiedEntities.get(categoryID + "");
    }

    public Double getCorrectedPValueForCategory(Integer categoryID) {

        return Double.parseDouble(correctionMap.get(categoryID + ""));
    }

    public Set<Integer> getCategories() {

        return testMap.keySet();
    }

    private void computeElementsPerCategory() {

        this.classifiedEntities = new HashMap<String, HashSet<String>>();
        Iterator it2 = params.getSelectedNodes().iterator();
        while (it2.hasNext()) {
            String name = it2.next() + "";
            HashSet tmp = params.getAlias().get(name);
            if (tmp != null) {
                Iterator it = tmp.iterator();
                while (it.hasNext()) {
                    int[] nodeClassifications = params.getAnnotation().getClassifications(it.next() + "");
                    for (int k = 0; k < nodeClassifications.length; k++) {
                        String cat = new Integer(nodeClassifications[k]).toString();
                        if (!classifiedEntities.containsKey(cat)) {
                            HashSet catset = new HashSet();
                            classifiedEntities.put(cat, catset);
                        }
                        ((HashSet) classifiedEntities.get(cat)).add(name);
                    }
                }
            }
        }
    }

    public void loadDesiredElementsForEnrichmentFromFile(String fileName) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fileName)));

        String container = new String();
        HashSet<String> inputNodes = new HashSet<String>();
        HashMap<String, Double> inputWeights = new HashMap<String, Double>();

        String line;
        while ((line = br.readLine()) != null) {
            container += line;

//            String content[] = new String[line.split("\\s+").length];
//            content = line.split("\\s+");
            String[] content = line.split("\\t");

            
            inputNodes.add(content[0]);
            if (content.length == 2) {
                String name[] = new String[content[0].split(":").length];
                name = content[0].split(":");

                inputWeights.put(content[0], Double.parseDouble(content[1]));
            }
        }
        this.params.setTextInput(container);
        this.params.setSelectedNodes(inputNodes);

        this.params.setWeights(inputWeights);
    }
    
    //If input is a string array, use this, otherwise use method loadDesiredElementsForEnrichmentFromInput
    public void loadDesiredElementsForEnrichmentFromInputOld(String[] input) throws IOException {

        String container = new String();
        HashSet<String> inputNodes = new HashSet<String>();
        HashMap<String, Double> inputWeights = new HashMap<String, Double>();

        for (int i=0; i<input.length; i+=2) {
            container += input[i].concat(input[i+1]);

            inputNodes.add(input[i]);
                String name[] = new String[input[i].split(":").length];
                name = input[i].split(":");
                
                inputWeights.put(input[i], Double.valueOf(input[i+1]));
        }
        this.params.setTextInput(container);
        this.params.setSelectedNodes(inputNodes);

        this.params.setWeights(inputWeights);
    }

    //Alternative method to be used for the web-project, because the user-input will be passed in the form of HashMap 
    public void loadDesiredElementsForEnrichmentFromInput(HashMap<String, String> input) throws IOException {

        String container = new String();
        HashSet<String> inputNodes = new HashSet<String>();
        HashMap<String, Double> inputWeights = new HashMap<String, Double>();

        for (String chebiId : input.keySet()) {
            container += chebiId.concat(input.get(chebiId));

            inputNodes.add(chebiId);
                String name[] = new String[chebiId.split(":").length];
                name = chebiId.split(":");
                
    			Double weight = Double.valueOf(input.get(chebiId));

    			inputWeights.put(chebiId, weight);
        }
        this.params.setTextInput(container);
        this.params.setSelectedNodes(inputNodes);

        this.params.setWeights(inputWeights);
    }
    
    public void setAllNodes() {

        String[] nodes = params.getAnnotation().getNames();
        // HashSet for storing the canonical names
        HashSet canonicalNameVector = new HashSet();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null && (nodes[i].length() != 0)) {
                canonicalNameVector.add(nodes[i].toUpperCase());
            }
        }

        params.setAllNodes(canonicalNameVector);
    }

    public void setParameters(BingoParameters parameters) {

        this.params = parameters;
    }
}
