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
package org.sybila.parasim.computation.cycledetection.cpu;

import org.sybila.parasim.computation.cycledetection.api.CycleDetectorFactory;
import org.sybila.parasim.model.trajectory.PointComparator;

/**
 * A factory to create Brent's cycle detectors.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class BrentsCycleDetectorFactory implements CycleDetectorFactory<BrentsCycleDetector> {

    private PointComparator comparator;

    public BrentsCycleDetectorFactory(PointComparator comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("The parameter comparator is null.");
        }
        this.comparator = comparator;
    }

    @Override
    public BrentsCycleDetector create() {
        return new BrentsCycleDetector(comparator);
    }

    /**
     * @return the comparator
     */
    public PointComparator getComparator() {
        return comparator;
    }

    /**
     * @param comparator the comparator to set
     */
    public void setComparator(PointComparator comparator) {
        this.comparator = comparator;
    }
}
