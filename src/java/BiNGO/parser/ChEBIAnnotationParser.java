/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package BiNGO.parser;

import BiNGO.BingoParameters;
import BiNGO.methods.BingoAlgorithm;
import BiNGO.reader.BiNGOOntologyChebiOboReader;
import BiNGO.reader.BiNGOOntologyFlatFileReader;
import BiNGO.reader.BiNGOOntologyOboReader;
import cytoscape.data.annotation.Annotation;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This is a decorator class for the AnnotationParser.
 *
 * @author pmoreno
 */
public class ChEBIAnnotationParser extends AnnotationParser {

    public ChEBIAnnotationParser(BingoParameters params, HashSet<String> genes) {

        super(params, genes);
    }

    //@Override
    //public void calculate() {}

    /**
     * Method that parses the custom annotation file into an annotation-object and
     * returns a string containing whether the operation is correct or not.
     * <p/>
     * In the ChEBI annotation parser, this will produce the chebi to chebi
     * entries file. The annotation in ChEBI needs to be loaded after the ontology is.
     *
     * @return string string with either loadcorrect or a parsing error.
     */
    @Override
    public String setCustomAnnotation() {
        //String fileString = params.getAnnotationFile();
        //annotation = null;

        String resultString = LOADCORRECT;

        HashMap<Integer, String> ontologyIDs2Names = fullOntology.getTerms();

        annotation = new Annotation(params.getSpecies(), "ChEBI", fullOntology);
        alias = new HashMap<String, String>();
        for (Integer id : ontologyIDs2Names.keySet()) {
            String entityName = "CHEBI:" + id;
            annotation.add(entityName, id);
            HashSet tmp = new HashSet();
            tmp.add(entityName);
            alias.put(entityName, tmp);
        }

        // TODO Is this correct??
        params.setAlias(alias);

        this.consistency = true;


        //if fileString contains "gene_association" then assume you're using GO Consortium annotation files
        /*if(fileString.contains("gene_association")){
            try {
                BiNGOConsortiumAnnotationReader readerAnnotation = new BiNGOConsortiumAnnotationReader(fileString, synonymHash, params, "Consortium", "GO");
                annotation = readerAnnotation.getAnnotation();
                if (readerAnnotation.getOrphans()) {
                    orphansFound = true;
                }
                if (readerAnnotation.getConsistency()) {
                    consistency = true;
                }
                alias = readerAnnotation.getAlias();
                resultString = LOADCORRECT;
            }
            catch (IllegalArgumentException e) {
                taskMonitor.setException(e, "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:");
                resultString = "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
            }
            catch (IOException e) {
                taskMonitor.setException(e, "Annotation file could not be located...");
                resultString = "Annotation file could not be located...";
            }
            catch (Exception e) {
                taskMonitor.setException(e, "");
                resultString = "" + e;
            }
        }
        else*//*{

            // flat file reader for custom annotation
            try {
                // In the ChEBI case we shouldn't be reading any other new file, we should just take the ontology and
                // make an annotation out of it.
                BiNGOAnnotationFlatFileReader readerAnnotation = new BiNGOAnnotationFlatFileReader(fileString, synonymHash);
                annotation = readerAnnotation.getAnnotation();
                if (readerAnnotation.getOrphans()) {
                   orphansFound = true;
                }
                if (readerAnnotation.getConsistency()) {
                    consistency = true;
                }
                alias = readerAnnotation.getAlias();
                resultString = LOADCORRECT;
            }
            catch (IllegalArgumentException e) {
                //taskMonitor.setException(e, "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:");
                resultString = "ANNOTATION FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
            }
            catch (IOException e) {
                //taskMonitor.setException(e, "Annotation file could not be located...");
                resultString = "Annotation file could not be located...";
            }
            catch (Exception e) {
                System.out.println(e);
                //taskMonitor.setException(e, "");
                resultString = "" + e;
            }
        }*/
/*        else{
          annotation = params.getAnnotation();
          alias = params.getAlias();
          resultString = LOADCORRECT;
          consistency = true;
        }
 */

        return resultString;
    }

    @Override
	public void calculate() {

        if (!params.isOntology_default()) {
            // always perform full remap for .obo files, allows definition of custom GOSlims
            if (params.getOntologyFile().endsWith(".obo")) {
                String loadFullOntologyString = setFullOntology();
                if (!loadFullOntologyString.equals(LOADCORRECT)) {
                    status = false;
                    //System.out.println("Your full ontology file contains errors " + loadFullOntologyString);
                }
                if (status == true) {
                    //check for cycles
                    checkOntology(fullOntology);
                }
            }

            if (status == true) {
                String loadOntologyString = setCustomOntology();

                // loaded a correct ontology file?
                if (!loadOntologyString.equals(LOADCORRECT)) {
                    status = false;
                    //System.out.println("Your ontology file contains errors " + loadOntologyString);
                }
                if (status == true) {
                    //check for cycles
                    checkOntology(ontology);
                    if (status = true) {
                        String loadAnnotationString;
                        if (!params.isAnnotation_default()) {
                            loadAnnotationString = setCustomAnnotation();
                        } else {
                            loadAnnotationString = setDefaultAnnotation();
                        }

                        // loaded a correct annotation file?
                        if (!loadAnnotationString.equals(LOADCORRECT)) {
                            status = false;
                            //System.out.println("Your annotation file contains errors " + loadAnnotationString);
                        }
                        // annotation consistent with ontology ?
                        if ((status == true) && (consistency == false)) {
                            status = false;
                            Exception e = new Exception();
                            //taskMonitor.setException(e, "None of the labels in your annotation match with the chosen ontology, please check their compatibility.");
                            System.out.println(
                                    "None of the labels in your annotation match with the chosen ontology, please check their compatibility.");
                        }
                        if (status == true) {
                            if (params.getOntologyFile().endsWith(".obo")) {
                                parsedAnnotation = remap(annotation, ontology, genes);
                            } else {
                                parsedAnnotation = customRemap(annotation, ontology, genes);
                            }
                        }
                    }
                }
            }
        } else {
            String loadAnnotationString;
            // load full ontology for full remap to GOSlim ontologies, and for defining synonymHash
            String loadFullOntologyString = setFullOntology();
            if (!loadFullOntologyString.equals(LOADCORRECT)) {
                status = false;
                //System.out.println("Your full ontology file contains errors " + loadFullOntologyString);
            }
            if (status == true) {
                //check for cycles
                checkOntology(fullOntology);
            }
            if (status == true) {
                String loadOntologyString = setDefaultOntology(synonymHash);
                if (!loadOntologyString.equals(LOADCORRECT)) {
                    status = false;
                    //System.out.println(loadOntologyString);
                }
                if (status == true) {
                    //check for cycles
                    checkOntology(ontology);
                    if (status == true) {
                        if (!params.isAnnotation_default()) {
                            loadAnnotationString = setCustomAnnotation();
                        } else {
                            loadAnnotationString = setDefaultAnnotation();
                        }

                        // loaded a correct annotation file?
                        if (!loadAnnotationString.equals(LOADCORRECT)) {
                            status = false;
                            // System.out.println(loadAnnotationString);
                        }

                        if ((status == true) && (consistency == false)) {
                            status = false;
                            Exception e = new Exception();
                            //taskMonitor.setException(e, "None of the labels in your annotation match with the chosen ontology, please check their compatibility.");
                            System.out.println(
                                    "None of the labels in your annotation match with the chosen ontology, please check their compatibility.");
                        }

                        if (status == true) {
                            // full remap not needed for non-Slim ontologies, instead custom remap
                            // bug 20/9/2005 changed annotationPanel to ontologyPanel
                            //if (params.getOntologyFile().equals(fullGoPath) || params.getOntologyFile().equals(processGoPath) || params.getOntologyFile().equals(functionGoPath) || params.getOntologyFile().equals(componentGoPath)){
                            //    parsedAnnotation = customRemap(annotation, ontology,genes);
                            //}
                            // full remap for Slim Ontologies
                            //else {
                            parsedAnnotation = remap(annotation, ontology, genes);
                            //}
                        }
                    }
                }
            }
        }
    }

    /**
     * Method that parses the ontology file into an ontology-object and
     * returns a string containing whether the operation is correct or not.
     *
     * @return string string with either loadcorrect or a parsing error.
     */
    @Override
    public String setCustomOntology() {

        String fileString = params.getOntologyFile();
        String namespace = params.getNameSpace();
        ontology = null;
        String resultString = "";

        //if fileString == null use ontology from Cytoscape
        //if(fileString != null){
        // obo file
        if (fileString.endsWith(".obo")) {
            try {
                BiNGOOntologyOboReader readerOntology = new BiNGOOntologyChebiOboReader(fileString, namespace);
                ontology = readerOntology.getOntology();
                if (ontology.size() == 0) {
                    throw (new IllegalArgumentException());
                } else {
                    // do not touch synonymHash, synonymHash of full .obo annotation will be used
                    //synonymHash = readerOntology.getSynonymHash();
                    resultString = LOADCORRECT;
                }
            } catch (IllegalArgumentException e) {
                //taskMonitor.setException(e, "Ontology file parsing error, please check file format and validity of namespace");
                resultString =
                        "ONTOLOGY FILE PARSING ERROR, PLEASE CHECK FILE FORMAT AND VALIDITY OF NAMESPACE:  \n" + e;
            } catch (IOException e) {
                //taskMonitor.setException(e, "Ontology file could not be located...");
                resultString = "Ontology file could not be located...";
            } catch (Exception e) {
                //taskMonitor.setException(e, "");
                resultString = "" + e;
            }
        } else {
            this.synonymHash = null;
            // flat file.
            try {
                BiNGOOntologyFlatFileReader readerOntology = new BiNGOOntologyFlatFileReader(fileString);
                ontology = readerOntology.getOntology();
                this.synonymHash = readerOntology.getSynonymHash();
                resultString = LOADCORRECT;
            } catch (IllegalArgumentException e) {
                //taskMonitor.setException(e, "Ontology file parsing error, please check file format");
                resultString = "ONTOLOGY FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
            } catch (IOException e) {
                //taskMonitor.setException(e, "Ontology file could not be located...");
                resultString = "Ontology file could not be located...";
            } catch (Exception e) {
                //taskMonitor.setException(e, "");
                resultString = "" + e;
            }
        }
        /*}
        else{
          ontology = params.getOntology();
          resultString = LOADCORRECT;
        }*/

        return resultString;
    }


    /**
     * Method that parses the ontology file into an ontology-object and
     * returns a string containing whether the operation is correct or not.
     *
     * @return string string with either loadcorrect or a parsing error.
     */
    @Override
	public String setFullOntology() {

        fullOntology = null;
        synonymHash = null;
        String resultString = "";

        if (params.getOntologyFile().endsWith(".obo")) {
            // read full ontology.
            try {
                BiNGOOntologyOboReader readerOntology =
                        new BiNGOOntologyChebiOboReader(params.getOntologyFile(), BingoAlgorithm.NONE);
                fullOntology = readerOntology.getOntology();
                if (fullOntology.size() == 0) {
                    throw (new IllegalArgumentException());
                } else {
                    synonymHash = readerOntology.getSynonymHash();
                    resultString = LOADCORRECT;
                }
            } catch (IllegalArgumentException e) {
                resultString =
                        "ONTOLOGY FILE PARSING ERROR, PLEASE CHECK FILE FORMAT AND VALIDITY OF NAMESPACE:  \n" + e;
            } catch (IOException e) {
                resultString = "Ontology file could not be located...";
            } catch (Exception e) {
                resultString = "" + e;
            }
        } else {
            // deserialize object
//            try {
//                BiNGOOntologyFlatFileReader readerOntology = new BiNGOOntologyFlatFileReader(params.getOntologyFile());
//                fullOntology = readerOntology.getOntology();
//                synonymHash = readerOntology.getSynonymHash();
//                resultString = LOADCORRECT;
//            } catch (IllegalArgumentException e) {
//                //taskMonitor.setException(e, "Full ontology file parsing error, please check file format");
//                resultString = "FULL ONTOLOGY FILE PARSING ERROR, PLEASE CHECK FILE FORMAT:  \n" + e;
//            } catch (IOException e) {
//                //taskMonitor.setException(e, "Full ontology file could not be located...");
//                resultString = "Full ontology file could not be located... ";
//            } catch (Exception e) {
//                //taskMonitor.setException(e, "");
//                resultString = "" + e;
//            }
        }
        return resultString;

    }
}
