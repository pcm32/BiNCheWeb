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

import java.io.FileWriter;
import java.io.Writer;

import net.sourceforge.metware.binche.graph.PngWriter;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import edu.uci.ics.jung.visualization.VisualizationImageServer;

/**
 * Class to write out JUNG graphs to SVG.
 */
public class SvgWriter {

    private static final Logger LOGGER = Logger.getLogger(PngWriter.class);

    private SVGGraphics2D svgGenerator;

    /**
     * Constructs the svg writer.
     */
    public SvgWriter() {

        // Get a DOMImplementation.
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        svgGenerator = new SVGGraphics2D(document);
    }

    /**
     * Writes the content from the vis. server to the file.
     *
     * @param server the visualisation server
     * @param fileName the file path
     */
    public void writeSvg(VisualizationImageServer server, String fileName) {

        // Ask the server to render into the SVG Graphics2D implementation.
//        server.paintAll(svgGenerator);

        // Finally, stream out SVG to the standard output using UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes
        try {
            Writer out = new FileWriter(fileName);
            svgGenerator.stream(out, useCSS);
            out.close();
        } catch (Exception exception) {
            LOGGER.log(Level.ERROR, "Error writing file: " + fileName + " " + exception.getMessage());
        }
    }
}
