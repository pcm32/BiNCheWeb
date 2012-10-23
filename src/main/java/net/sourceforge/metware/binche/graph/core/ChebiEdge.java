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

public class ChebiEdge {

    private String id;
    private double pValue;

    public ChebiEdge(String id, double pValue) {

        this.id = id;
        this.pValue = pValue;
    }

    public String getId() {

        return id;
    }

    @Override
	public String toString() {

        return pValue == 0 ? "" : String.format("%3.2e", pValue);
    }

    @Override public boolean equals(Object obj) {

        if (obj == null || !(obj instanceof ChebiEdge)) return false;

        ChebiEdge edge = (ChebiEdge) obj;
        if (edge.getId().equals(this.getId())) return true;

        return false;
    }
}