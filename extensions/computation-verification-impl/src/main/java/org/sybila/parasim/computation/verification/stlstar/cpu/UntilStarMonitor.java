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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.stl.UntilFormula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.util.Coordinate;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class UntilStarMonitor extends TemporalStarMonitor {

    private class RobustnessIndex {

        private final Coordinate.Builder coord;

        public RobustnessIndex(Coordinate frozen) {
            Validate.notNull(frozen);
            coord = new Coordinate.Builder(frozen);
        }

        public Coordinate get(int index) {
            coord.setCoordinate(0, index);
            return coord.create();
        }
    }
    private final StarMonitor left, right;

    public UntilStarMonitor(UntilFormula property, FormulaStarInfo info, StarMonitor leftSubMonitor, StarMonitor rightSubMonitor) {
        super(property, info);
        Validate.notNull(leftSubMonitor);
        Validate.notNull(rightSubMonitor);
        left = leftSubMonitor;
        right = rightSubMonitor;
    }

    @Override
    public Collection<Monitor> getSubmonitors() {
        return Arrays.asList((Monitor) left, (Monitor) right);
    }

    @Override
    protected List<Robustness> computeLine(Coordinate frozen) {
        List<Robustness> result = new ArrayList<>();

        RobustnessIndex index = new RobustnessIndex(frozen);
        int current = 0;
        int shortestSize = Math.min(left.size(), right.size());
        while (current < shortestSize) {
            int windowEnd = current;
            float currentTime = left.getRobustness(index.get(windowEnd)).getTime();
            float windowEndLeft = left.getRobustness(index.get(windowEnd)).getValue();
            while ((windowEnd < shortestSize - 1) && (left.getRobustness(index.get(windowEnd)).getTime() < currentTime + getInterval().getLowerBound())) {
                windowEnd++;
                windowEndLeft = Math.min(windowEndLeft, left.getRobustness(index.get(windowEnd)).getValue());
            }
            if (left.getRobustness(index.get(windowEnd)).getTime() < getInterval().getLowerBound()) {
                break;
            }

            float windowEndRight = right.getRobustness(index.get(windowEnd)).getValue();
            float currentRobustness = Math.min(windowEndLeft, windowEndRight);
            while ((windowEnd < shortestSize - 1) && (left.getRobustness(index.get(windowEnd + 1)).getTime() <= currentTime + getInterval().getUpperBound())) {
                windowEnd++;
                windowEndLeft = Math.min(windowEndLeft, left.getRobustness(index.get(windowEnd)).getValue());
                windowEndRight = right.getRobustness(index.get(windowEnd)).getValue();
                currentRobustness = Math.max(currentRobustness, Math.min(windowEndLeft, windowEndRight));
            }
            if ((left.getRobustness(index.get(windowEnd)).getTime() < currentTime + getInterval().getUpperBound()) && (windowEnd == shortestSize - 1)) {
                break;
            }
            result.add(new SimpleRobustness(currentRobustness, currentTime, getProperty()));
            current++;
        }
        return result;
    }
}
