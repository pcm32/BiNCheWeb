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

import java.awt.*;

public class ChebiVertex {

    private int id;
    private String chebiId;
    private Color color;
    private String chebiName;

    public ChebiVertex(int id, String chebiId, String chebiName) {

        this.id = id;
        this.chebiId = chebiId;
        this.chebiName = chebiName;

        color = new Color(151, 252, 151, 128);
    }

    public int getId() {

        return id;
    }

    public String getChebiId() {

        return chebiId;
    }

    public String getChebiName() {

        return chebiName;
    }

    public void setColor(Color color) {

        this.color = color;
    }

    public Color getColor() {

        return color;
    }

    @Override
	public String toString() {

        return (chebiName);
    }
}
