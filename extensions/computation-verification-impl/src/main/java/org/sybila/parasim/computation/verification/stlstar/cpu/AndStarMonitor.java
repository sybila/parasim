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

/**
 * Monitors logical conjunction of two subformulae.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class AndStarMonitor extends BinaryPointwiseStarMonitor {

    /**
     * Create conjunction monitor.
     *
     * @param property Monitored formula.
     * @param info Star information of root formula.
     * @param left Monitor of left formula.
     * @param right Monitor of right formula.
     */
    public AndStarMonitor(Formula property, FormulaStarInfo info, StarMonitor left, StarMonitor right) {
        super(property, info, left, right);
    }

    @Override
    protected float evaluate(Robustness left, Robustness right) {
        return Math.min(left.getValue(), right.getValue());
    }
}
