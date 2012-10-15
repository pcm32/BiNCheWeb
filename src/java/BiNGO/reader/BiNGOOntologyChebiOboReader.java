/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BiNGO.reader;

import cytoscape.data.annotation.Ontology;
import cytoscape.data.annotation.OntologyTerm;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

/**
 * @author pmoreno
 */
public class BiNGOOntologyChebiOboReader extends BiNGOOntologyOboReader {

    public BiNGOOntologyChebiOboReader(File file,
                                       String namespace) throws IllegalArgumentException, IOException, Exception {

        this(file.getPath(), namespace);
    }

    public BiNGOOntologyChebiOboReader(String filename,
                                       String namespace) throws IllegalArgumentException, IOException, Exception {

        super(filename, namespace);
    }

    @Override
	protected int parseHeader() throws Exception {

        int i = 0;
        while (!lines[i].trim().equals("[Term]")) {
            i++;
        }
        curator = "unknown";
        ontologyType = "unknown";
        return i;

    } // parseHeader
//-------------------------------------------------------------------------

    @Override
	protected void parse(int c) throws Exception {

        ontology = new Ontology(curator, ontologyType);
        fullOntology = new Ontology(curator, ontologyType);
        int i = c;
        while (i < lines.length && !lines[i].trim().equals("[Typedef]")) {
            i++;
            String name = new String();
            String id = new String();
            HashSet<String> namespaces = new HashSet<String>();
            HashSet<String> alt_id = new HashSet<String>();
            HashSet<String> is_a = new HashSet<String>();
            //HashSet<String> part_of = new HashSet<String>();
            HashSet<String> has_part = new HashSet<String>();
            HashSet<String> has_role = new HashSet<String>();
            boolean obsolete = false;
            while (!lines[i].trim().equals("[Term]") && !lines[i].trim().equals("[Typedef]") && i < lines.length) {
                if (!lines[i].trim().isEmpty()) {
                    String ref = lines[i].substring(0, lines[i].indexOf(":")).trim();
                    String value = lines[i].substring(lines[i].indexOf(":") + 1).trim();
                    if (ref.equals("name")) {
                        name = value.trim();
                    } else if (ref.equals("namespace")) {
                        namespaces.add(value.trim());
                    } else if (ref.equals("subset")) {
                        namespaces.add(value.trim());
                    } else if (ref.equals("id")) {
                        id = value.trim().substring(value.indexOf(":") + 1);
                    } else if (ref.equals("alt_id")) {
                        alt_id.add(value.trim().substring(value.indexOf(":") + 1));
                    } else if (ref.equals("is_a")) {
                        is_a.add(value.split("!")[0].trim().substring(value.indexOf(":") + 1));
                    } else if (ref.equals("relationship")) {
                        //if(value.startsWith("part_of")){
                        //    part_of.add(value.substring(7).split("!")[0].trim().substring(value.indexOf(":")+1));
                        //}
                        if (value.startsWith("has_part")) {
                            has_part.add(value.substring(value.indexOf(":") + 1));
                        } else if (value.startsWith("has_role")) {
                            has_role.add(value.substring(value.indexOf(":") + 1));
                        }

                    } else if (ref.equals("is_obsolete")) {
                        if (value.trim().equals("true")) {
                            obsolete = true;
                        }
                    }
                }
                i++;
            }
            if (obsolete == false) {
                //for (String n : this.namespaces) {
                //if (n.equals(BingoAlgorithm.NONE) || namespaces.contains(n)) {
                // For the ChEBI namespace
                Integer id2 = new Integer(id);
                synonymHash.put(id2, id2);
                OntologyTerm term;
                if (!ontology.containsTerm(id2)) {
                    term = new OntologyTerm(name, id2);
                    ontology.add(term);
                    fullOntology.add(term);
                } else {
                    term = ontology.getTerm(id2);
                }
                for (String s : alt_id) {
                    synonymHash.put(new Integer(s), id2);
                }
                for (String s : is_a) {
                    term.addParent(new Integer(s));
                }
                for (String s : has_role) {
                    term.addContainer(new Integer(s));
                }
                //for(String s:part_of){
                //    term.addContainer(new Integer(s));
                //}
                for (String s : has_part) { // elements in has part
                    // are sub parts of the term that we are looking
                    // for. Hence, we get the "smaller" term and
                    // add the current term as a container for it.
                    Integer containedID = new Integer(s);
                    OntologyTerm containedTerm;
                    if (ontology.containsTerm(containedID)) {
                        containedTerm = ontology.getTerm(containedID);
                    } else {
                        containedTerm = new OntologyTerm(name, containedID);
                        ontology.add(containedTerm);
                        fullOntology.add(containedTerm);
                    }

                    containedTerm.addContainer(term.getId());
                }

                /*} else {
                    Integer id2 = new Integer(id);
                    OntologyTerm term = new OntologyTerm(name, id2);
                    if (!fullOntology.containsTerm(id2)) {
                        fullOntology.add(term);
                        for (String s : is_a) {
                            term.addParent(new Integer(s));
                        }
                        //for (String s : part_of) {
                        //    term.addContainer(new Integer(s));
                        //}
                    }
                }*/
                //}
            }
        }
        //explicitely reroute all connections (parent-child relationships) that are missing in subontologies like GOSlim
        //avoid transitive connections
        // TODO This is an undesired bias towards gene ontology.
        /*if (!namespace.equals("biological_process") && !namespace.equals("molecular_function") && !namespace.equals("cellular_component") && !namespace.equals(BingoAlgorithm.NONE)) {
            for (Integer j : (Set<Integer>) ontology.getTerms().keySet()) {
                OntologyTerm o = ontology.getTerm(j);
                HashSet<OntologyTerm> ancestors = findNearestAncestors(new HashSet<OntologyTerm>(), j);
                HashSet<OntologyTerm> prunedAncestors = new HashSet<OntologyTerm>(ancestors);
                for (OntologyTerm o2 : ancestors) {
                    HashSet<OntologyTerm> o2Ancestors = getAllAncestors(new HashSet<OntologyTerm>(), o2);
                    for (OntologyTerm o3 : o2Ancestors) {
                        if (ancestors.contains(o3)) {
                            System.out.println("removed " + o3.getName());
                            prunedAncestors.remove(o3);
                        }
                    }
                }
                for (OntologyTerm o2 : prunedAncestors) {
                    o.addParent(o2.getId());
                }
            }
        }*/

//        makeOntologyFile(System.getProperty("user.home"));

    } // read
}
