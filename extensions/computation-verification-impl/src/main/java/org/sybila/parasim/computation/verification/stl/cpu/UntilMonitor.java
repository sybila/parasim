/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.sybila.parasim.computation.verification.cpu.AbstractMonitor;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.stl.FormulaInterval;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class UntilMonitor extends AbstractMonitor {

    private final List<Robustness> robustnesses;
    private final Monitor left;
    private final Monitor right;

    public UntilMonitor(Property property, Monitor left, Monitor right, FormulaInterval interval) {
        super(property);
        if (left == null) {
            throw new IllegalArgumentException("The parameter [left] is null.");
        }
        if (right == null) {
            throw new IllegalArgumentException("The parameter [right] is null.");
        }
        if (interval == null) {
            throw new IllegalArgumentException("The parameter [interval] is null.");
        }
        this.left = left;
        this.right = right;
        robustnesses = precompute(left, right, interval, property);
    }

    @Override
    public Robustness getRobustness(int index) {
        return robustnesses.get(index);
    }

    @Override
    public Collection<Monitor> getSubmonitors() {
        return Arrays.asList(left, right);
    }

    @Override
    public int size() {
        return robustnesses.size();
    }

    private List<Robustness> precompute(Monitor left, Monitor right, FormulaInterval interval, Property property) {
        int current = 0;
        boolean finished = false;
        List<Robustness> precomputed = new ArrayList<>();
        int shortestSize = Math.min(left.size(), right.size());
        while (current < shortestSize) {
            int windowEnd = current;
            float currentTime = left.getRobustness(windowEnd).getTime();
            float windowEndLeft = left.getRobustness(windowEnd).getValue();
            while (windowEnd < shortestSize - 1 && left.getRobustness(windowEnd).getTime() < currentTime + interval.getLowerBound()) {
                windowEnd++;
                windowEndLeft = Math.min(windowEndLeft, left.getRobustness(windowEnd).getValue());
            }
            if (left.getRobustness(windowEnd).getTime() < interval.getLowerBound()) {
                break;
            }
            float windowEndRight = right.getRobustness(windowEnd).getValue();
            float currentRobustness = Math.min(windowEndLeft, windowEndRight);
            while(windowEnd < shortestSize - 1 && left.getRobustness(windowEnd + 1).getTime() <= currentTime + interval.getUpperBound()) {
                windowEnd++;
                windowEndLeft = Math.min(windowEndLeft, left.getRobustness(windowEnd).getValue());
                windowEndRight = right.getRobustness(windowEnd).getValue();
                currentRobustness = Math.max(currentRobustness, Math.min(windowEndLeft, windowEndRight));
            }
            if (left.getRobustness(windowEnd).getTime() < currentTime + interval.getUpperBound() && windowEnd == shortestSize - 1) {
                break;
            }
            precomputed.add(new SimpleRobustness(currentRobustness, left.getRobustness(current).getTime(), property));
            current++;
        }
        return precomputed;
    }

}
