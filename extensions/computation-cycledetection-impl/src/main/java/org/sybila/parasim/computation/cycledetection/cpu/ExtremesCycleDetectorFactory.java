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
 * A factory to create new ExtremesCycleDetectors with different parameter
 * settings.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class ExtremesCycleDetectorFactory implements CycleDetectorFactory<ExtremesCycleDetector> {

    private PointComparator comparator;
    private int capacity;
    private ExtremesMode[] modes;

    public ExtremesCycleDetectorFactory(PointComparator comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("The parameter comparator is null.");
        }
        this.comparator = comparator;
        this.capacity = 10;
        this.modes = new ExtremesMode[comparator.getDimension()];
        for (int i = 0; i < comparator.getDimension(); i++) {
            modes[i] = ExtremesMode.EXTREME_BOTH;
        }
    }

    public ExtremesCycleDetector create() {
        return new ExtremesCycleDetector(capacity, comparator, modes);
    }

    /**
     * @return the comparator
     */
    public PointComparator getComparator() {
        return comparator;
    }

    /**
     * Sets a new PointComparator that will be used for creating new CycleDetectors.
     * Together with this the modes array is reset to have the same number of
     * dimensions as the comparator and is filled with ExtremesMode.EXTREME_BOTH.
     *
     * @param comparator the comparator to set
     */
    public void setComparator(PointComparator comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("The parameter comparator is null.");
        }
        this.comparator = comparator;
        modes = new ExtremesMode[comparator.getDimension()];
        for (int i = 0; i < comparator.getDimension(); i++) {
            modes[i] = ExtremesMode.EXTREME_BOTH;
        }
    }

    /**
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * @param capacity the capacity to set
     */
    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("The parameter capacity must be positive.");
        }
        this.capacity = capacity;
    }

    /**
     * @param index Index of dimension who's mode to return.
     * @return Mode of dimension with given index.
     */
    public ExtremesMode getModes(int index) {
        if (index < 0 || index >= modes.length) {
            throw new IllegalArgumentException("The parameter index must be in range [0, " + modes.length + ")");
        }
        return modes[index];
    }

    /**
     * @param index
     * @param mode The new mode of dimension with given index.
     */
    public void setModes(int index, ExtremesMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("The parameter mode is null.");
        }
        if (index < 0 || index >= modes.length) {
            throw new IllegalArgumentException("The parameter index must be in range [0, " + modes.length + ")");
        }
        this.modes[index] = mode;
    }
}
