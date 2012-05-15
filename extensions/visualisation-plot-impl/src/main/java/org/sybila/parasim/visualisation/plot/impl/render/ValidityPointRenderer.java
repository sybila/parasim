/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.visualisation.plot.impl.render;

import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 * Renders points as circles of two colors according to the extension configuration.
 * Points with robustness equal to zero are omitted.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ValidityPointRenderer extends ZeroRemover {

    /**
     * Initialize this renderer according to configuration:
     * <ul>
     *  <li>{@link ResultPlotterConfiguration#getPointRadius()} -- size (radius) of points.</li>
     *  <li>{@link ResultPlotterConfiguration#getPointColorValid()} -- color of points with positive robustness.</li>
     *  <li>{@link ResultPlotterConfiguration#getPointColorInvalid()} -- color of points with negative robustness.</li>
     * </ul>
     * @param conf This extension configuration.
     */
    public ValidityPointRenderer(ResultPlotterConfiguration conf) {
        super(new ColorPointRenderer(new CirclePointRenderer(conf.getPointRadius()),
                conf.getPointColorValid(), conf.getPointColorInvalid()), conf);
    }
}
