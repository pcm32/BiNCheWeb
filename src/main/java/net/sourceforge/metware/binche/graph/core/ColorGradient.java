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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility class to return a shade of green based on the linear function calculated from the input values.
 */
public class ColorGradient {

    private final double m;
    private final double b;

    private static final double MAX_ALPHA = 50;
    private static final double MIN_ALPHA = 255;

    /**
     * Constructs the gradient calculator and retrieves the linear function.
     *
     * @param values set of values containing the minimum and maximum possible value
     */
    public ColorGradient(Collection<Double> values) {

        List<Double> valueList;
        if (values instanceof List) {
            valueList = (List) values;
        } else {
            valueList = new ArrayList(values);
        }
        Collections.sort(valueList);

        double min = valueList.get(0);
        double max = valueList.get(valueList.size() - 1);

        m = (MAX_ALPHA - MIN_ALPHA) / (max - min);
        b = MAX_ALPHA - (m * max);
    }

    /**
     * Gets the shade of green with alpha based on value
     *
     * @param value the input value
     * @return the shade of green
     */
    public Color getGradientColor(double value) {

        int alpha = (int) (m * value + b);
        return new Color(255, 69, 0, alpha);
    }
}
