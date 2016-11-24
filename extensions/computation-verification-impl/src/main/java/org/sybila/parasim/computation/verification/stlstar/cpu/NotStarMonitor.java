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
package org.sybila.parasim.computation.verification.stlstar.cpu;

import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.util.Coordinate;

/**
 * Monitors a negation of subformula.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class NotStarMonitor extends NonTemporalUnaryStarMonitor {

    /**
     * Create a monitor for a negation of subformula.
     *
     * @param property This formula.
     * @param info Star information about root formula.
     * @param subMonitor Monitor of subformula.
     */
    public NotStarMonitor(Formula property, FormulaStarInfo info, StarMonitor subMonitor) {
        super(property, info, subMonitor);
    }

    @Override
    protected Robustness computeRobustness(Coordinate index) {
        return getSubMonitor().getRobustness(index).invert();
    }
}
