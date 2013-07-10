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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.metware.binche.BiNChe;
import net.sourceforge.metware.binche.graph.*;

import net.sourceforge.metware.binche.loader.BiNChEOntologyPrefs;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import BiNGO.BingoParameters;
import BiNGO.ParameterFactory;

/**
 * @author Stephan Beisken, Pablo Moreno, Bhavana Harsha, Janna Hastings
 */
public class BiNCheExecWeb {

    private static final Logger LOGGER = Logger.getLogger(BiNCheExecWeb.class);

    private Properties binCheProps;
	private Preferences binchePrefs;
    private Map<String,String> targetNameToOntologyFileMap = new HashMap<String, String>();
    private Map<String,String> targetNameToAnnotationFileMap = new HashMap<String, String>();
    
    public BiNCheExecWeb() {
    	binCheProps = new Properties();
	    binchePrefs  = Preferences.userNodeForPackage(BiNChe.class);
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
			    String ontologyFileName = binCheProps.getProperty("target.ontologyFile."+i,getOntology(i));
    			if (ontologyFileName != null) {
    				targetNameToOntologyFileMap.put(targetType, ontologyFileName);
    			}
    			String annotationFileName = binCheProps.getProperty("target.annotationFile."+i,getAnnotation(i));
    			if (annotationFileName != null) {
    				targetNameToAnnotationFileMap.put(targetType, annotationFileName);
    			}
    		}
    	}
    }

	private String getAnnotation(Integer fileNum) {
		if(fileNum==2) {
			return binchePrefs.get(BiNChEOntologyPrefs.RoleAnnot.name(),null);
		}
		return null;
	}

	private String getOntology(Integer fileNum) {
		if(fileNum==1) {
			return binchePrefs.get(BiNChEOntologyPrefs.StructureOntology.name(), null);
		} else if(fileNum==2) {
			return binchePrefs.get(BiNChEOntologyPrefs.RoleOntology.name(),null);
		} else if (fileNum==3) {
			return binchePrefs.get(BiNChEOntologyPrefs.RoleAndStructOntology.name(),null);
		}
		return null;
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
        if (!ontologyFile.exists()) {
        	//problem!
        	throw new FileNotFoundException("Unable to open ontology file: "+ontologyFile.getName());
        }
        System.out.println("Got ontology file: "+ontologyFileName);
        
        String annotationFileName = targetNameToAnnotationFileMap.get(target);
        if (annotationFileName != null) {
        	File annotationFile = new File(annotationFileName);
            if (!annotationFile.exists()) {
            	//problem!
            	throw new FileNotFoundException("Unable to open annotation file: "+annotationFile.getName());
            }
        }
        System.out.println("Got annotation file name: "+annotationFileName);
        
        LOGGER.log(Level.INFO, "Setting parameters ...");
        
        //Different parameters for weighted and plain analysis
        BingoParameters parametersChEBIBin = analysisType.equals("weighted") || analysisType.equals("fragment")?
        		ParameterFactory.makeParametersForChEBISaddleSum(ontologyFileName) :
        		ParameterFactory.makeParametersForChEBIBinomialOverRep(ontologyFileName);
        
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

        ChebiGraph chebiGraph = new ChebiGraph(binche.getEnrichedNodes(), binche.getOntology(), binche.getInputNodes());


        PrunningStrategy strategy;
        if(analysisType.equals("fragment"))
            strategy = new FragmentEnrichPruningStrategy();
        else if(analysisType.equals("weighted"))
            strategy = new WeightedEnrichPruningStrategy();
        else
            strategy = new PlainEnrichPruningStrategy();

        if(chebiGraph.getVertexCount()>40) {
            int removedVertices = strategy.applyStrategy(chebiGraph);
            System.out.println("Removed vertices : "+removedVertices);
        }
        int finalVertices = chebiGraph.getVertexCount();


        System.out.println("Final vertices : " + (finalVertices));

        //Convert the chebi Graph to a JSON Object for display on webapp
        JSONChEBIGraphConverter converter = new JSONChEBIGraphConverter();
        converter.setChebiGraphAsJSON(chebiGraph, request);

        request.getSession().setAttribute("chebiGraph",chebiGraph);

        LOGGER.log(Level.INFO, "############ Stop ############");

        response.setContentType("text/html");

    }

    private List<ChEBIGraphPruner> getPruners() {
        return Arrays.asList(new MoleculeLeavesPruner(), new LowPValueBranchPruner(0.05)
    , new LinearBranchCollapserPruner(), new RootChildrenPruner(3,false), new ZeroDegreeVertexPruner());
    }

    private List<ChEBIGraphPruner> getPrunersForFragmentAnalysis() {
        return Arrays.asList(new LowPValueBranchPruner(0.05), new LinearBranchCollapserPruner(), 
                new RootChildrenPruner(3,false), new ZeroDegreeVertexPruner());
    }




}
