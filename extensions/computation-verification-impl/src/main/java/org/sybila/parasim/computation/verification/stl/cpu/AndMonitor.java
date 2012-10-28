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
package org.sybila.parasim.computation.verification.stl.cpu;

import java.util.Collection;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class AndMonitor extends AbstractBinaryPropositionalMonitor {

    public AndMonitor(Property property, Monitor left, Monitor right, Collection<Integer> consideredDimensions) {
        super(property, left, right, consideredDimensions);
    }

    @Override
    protected Robustness evaluate(Robustness left, Robustness right, Collection<Integer> consideredDimensions) {
        return new SimpleRobustness(Math.min(left.getValue(), right.getValue()), left.getTime(), consideredDimensions);
    }

}
