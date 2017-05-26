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
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.stl.TemporalFormula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.util.Coordinate;
import org.sybila.parasim.util.LemireDeque;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class UnaryTemporalStarMonitor extends TemporalStarMonitor {

    private static enum RobustnessComparator implements Comparator<Robustness> {

        GLOBALLY {

            @Override
            public int compare(Robustness r1, Robustness r2) {
                return Float.compare(r2.getValue(), r1.getValue());
            }
        }, FUTURE {

            @Override
            public int compare(Robustness r1, Robustness r2) {
                return Float.compare(r1.getValue(), r2.getValue());
            }
        };
    }
    private final Comparator<Robustness> comparator;
    private final StarMonitor sub;

    public UnaryTemporalStarMonitor(TemporalFormula property, FormulaStarInfo info, StarMonitor subMonitor, Comparator<Robustness> comparator) {
        super(property, info);
        Validate.notNull(subMonitor);
        sub = subMonitor;
        this.comparator = comparator;
    }

    @Override
    protected List<Robustness> computeLine(Coordinate frozen) {
        Deque<Robustness> deque = new LemireDeque<>(comparator);
        List<Robustness> result = new ArrayList<>();
        Iterator<Robustness> window = new FrozenTimeRobustnessIterator(sub, frozen);
        Iterator<Robustness> current = new FrozenTimeRobustnessIterator(sub, frozen);
        float currentTime = current.next().getTime();

        while (window.hasNext()) {
            Robustness memory = null;
            boolean endReached = false;

            // push new points //
            while (window.hasNext() && !endReached) {
                memory = window.next();

                // check upper time bound //
                if (memory.getTime() <= currentTime + getInterval().getUpperBound()) {
                    deque.offer(memory);
                    if (memory.getTime() == currentTime + getInterval().getUpperBound()) {
                        endReached = true;
                    }
                    memory = null;
                } else {
                    endReached = true;
                }
            }

            if (!endReached) {
                return result;
            }

            // remove useless points //
            while (!deque.isEmpty() && deque.peekFirst().getTime() < currentTime + getInterval().getLowerBound()) {
                deque.remove();
            }

            // get the first robustness //
            Robustness found = deque.peekFirst();
            result.add(new SimpleRobustness(found.getValue(), currentTime, getProperty()));
            currentTime = current.next().getTime();
            if (memory != null) {
                deque.offer(memory);
            }
        }
        return result;
    }

    @Override
    public Collection<Monitor> getSubmonitors() {
        return Arrays.asList((Monitor) sub);
    }

    public static StarMonitor getGloballyMonitor(TemporalFormula property, FormulaStarInfo info, StarMonitor subMonitor) {
        return new UnaryTemporalStarMonitor(property, info, subMonitor, RobustnessComparator.GLOBALLY);
    }

    public static StarMonitor getFutureMonitor(TemporalFormula property, FormulaStarInfo info, StarMonitor subMonitor) {
        return new UnaryTemporalStarMonitor(property, info, subMonitor, UnaryTemporalStarMonitor.RobustnessComparator.FUTURE);
    }
}
