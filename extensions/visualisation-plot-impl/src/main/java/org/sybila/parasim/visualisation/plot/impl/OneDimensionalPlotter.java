/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.api.PlotterWindowListener;
import org.sybila.parasim.visualisation.plot.api.MouseOnResultListener;

/**
 * Plotter containing only one dimension. Prints a message reporting this fact.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OneDimensionalPlotter implements Plotter {

    @Override
    public void addMouseOnResultListener(MouseOnResultListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeMouseOnResultListener(MouseOnResultListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addPlotterWindowListener(PlotterWindowListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removePlotterWindowListener(PlotterWindowListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void plot() {
        System.out.println("One dimension plotting not supported.");
    }
}
