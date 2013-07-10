package net.sourceforge.metware.binche.execs;

import net.sourceforge.metware.binche.graph.ChebiGraph;
import net.sourceforge.metware.binche.graph.ChebiVertex;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pmoreno
 * Date: 8/7/13
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
public class TableWriter {

    private static final Logger LOGGER = Logger.getLogger(TableWriter.class);

    private OutputStream outputStream;

    public TableWriter(OutputStream pathToTable) {
        this.outputStream = pathToTable;
        //LOGGER.info("Writing file to outputStream : "+ outputStream);
    }


    public void write(ChebiGraph chebiGraph) {
        List<ChebiVertex> vertexes = new ArrayList<ChebiVertex>(chebiGraph.getVertexCount());
        for (ChebiVertex vertex : chebiGraph.getVertices()) {
            vertexes.add(vertex);
        }

        Collections.sort(vertexes,new VertexComparatorByPValue());
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write("ChEBI_ID\tChEBI_Name\tCorr-PValue\tPValue\tFold\tSamplePercentage\n");
            for (ChebiVertex vertex : vertexes) {
                writer.write("CHEBI:"+vertex.getChebiId()+"\t"+vertex.getChebiName()
                        +"\t"+vertex.getCorrPValue()+"\t"+vertex.getpValue()+"\t"+vertex.getFoldOfEnrichment()
                        +"\t"+vertex.getSamplePercentage()+"\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Could not write table to file : ",e);
        }
    }

    private class VertexComparatorByPValue implements Comparator<ChebiVertex> {

        @Override
        public int compare(ChebiVertex chebiVertex, ChebiVertex chebiVertex2) {
            if(chebiVertex.getCorrPValue()!=null && chebiVertex2.getCorrPValue()!=null) {
                return chebiVertex.getCorrPValue().compareTo(chebiVertex2.getCorrPValue());
            } else if(chebiVertex.getpValue()!=null && chebiVertex2.getpValue()!=null) {
                return chebiVertex.getpValue().compareTo(chebiVertex2.getpValue());
            } else if(chebiVertex.getCorrPValue()!=null && chebiVertex2.getCorrPValue()==null) {
                return -1;
            } else if(chebiVertex.getCorrPValue()==null && chebiVertex2.getCorrPValue()!=null) {
                return 1;
            } else if(chebiVertex.getpValue()!=null && chebiVertex2.getpValue()==null) {
                return -1;
            } else if(chebiVertex.getpValue()==null && chebiVertex2.getpValue()!=null) {
                return 1;
            }
            return 0;
        }
    }
}
