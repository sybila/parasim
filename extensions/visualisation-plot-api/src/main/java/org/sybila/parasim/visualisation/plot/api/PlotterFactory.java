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
package org.sybila.parasim.visualisation.plot.api;

import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * Creates {@link Plotter} objects from {@link VerificationResult}.
 * Core class of extension -- should be created at initialization.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface PlotterFactory {

    /**
     * Creates a plotter from result.
     * @param result Object to be plotted.
     * @param mapping Mapping of names to variables (for labels).
     * @return Plotter which plots designated result.
     */
    public Plotter getPlotter(VerificationResult result, PointVariableMapping mapping);
}
