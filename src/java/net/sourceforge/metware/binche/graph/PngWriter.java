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
package net.sourceforge.metware.binche.graph;

import edu.uci.ics.jung.visualization.VisualizationImageServer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class PngWriter {

    private static final Logger LOGGER = Logger.getLogger(PngWriter.class);

    public void writePng(VisualizationImageServer server, String fileName, Dimension dim) {

        Image image = server.getImage(new Point.Double(0, 0), dim);
        File imageFile = new File(fileName);

        try {
            ImageIO.write(toBufferedImage(image), "png", imageFile);
        } catch (Exception exception) {
            LOGGER.log(Level.ERROR, "Error writing file: " + fileName + " " + exception.getMessage());
        }
    }

    private static BufferedImage toBufferedImage(Image src) {

        int w = src.getWidth(null);
        int h = src.getHeight(null);
        int type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage dest = new BufferedImage(w, h, type);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return dest;
    }
}
