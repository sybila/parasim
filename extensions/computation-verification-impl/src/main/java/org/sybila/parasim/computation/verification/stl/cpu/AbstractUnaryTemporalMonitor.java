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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.cpu.AbstractMonitor;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.stl.FormulaInterval;
import org.sybila.parasim.util.LemireDeque;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractUnaryTemporalMonitor extends AbstractMonitor {

    private final List<Robustness> robustnesses;
    private final Monitor suMonitor;
    private final Collection<Integer> consideredDimensions;

    public AbstractUnaryTemporalMonitor(Property property, Monitor subMonitor, FormulaInterval interval, Collection<Integer> consideredDimensions) {
        super(property);
        Validate.notNull(interval);
        Validate.notNull(subMonitor);
        Validate.notNull(consideredDimensions);
        this.suMonitor = subMonitor;
        this.robustnesses = precomputeRobustness(subMonitor, interval);
        this.consideredDimensions = consideredDimensions;
    }

    @Override
    public Robustness getRobustness(int index) {
        return robustnesses.get(index);
    }

    @Override
    public Collection<Monitor> getSubmonitors() {
        return Arrays.asList(suMonitor);
    }

    @Override
    public int size() {
        return robustnesses.size();
    }

    protected abstract Comparator<Robustness> createComparator();

    private List<Robustness> precomputeRobustness(Monitor subMonitor, FormulaInterval interval) {
        Deque<Robustness> lemireDeque = new LemireDeque<>(createComparator());
        List<Robustness> precomputed = new ArrayList<>();
        Iterator<Robustness> window = subMonitor.iterator();
        Iterator<Robustness> current = subMonitor.iterator();
        int currentIndex = 0;
        float currentTime = current.next().getTime();
        while (window.hasNext()) {
            Robustness memory = null;
            boolean windowEndReached = false;
            // push new points
            while (window.hasNext() && !windowEndReached) {
                memory = window.next();
                // check whether the time upper bound is reached
                if (memory.getTime() < currentTime + interval.getUpperBound()) {
                    lemireDeque.offer(memory);
                    memory = null;
                } else if (memory.getTime() == currentTime + interval.getUpperBound()) {
                    lemireDeque.offer(memory);
                    memory = null;
                    windowEndReached = true;
                } else {
                    windowEndReached = true;
                }
            }
            // check whether the window end has been reached
            if (!windowEndReached) {
                return precomputed;
            }
            // remove useless points
            while (!lemireDeque.isEmpty() && lemireDeque.peekFirst().getTime() < currentTime + interval.getLowerBound()) {
                lemireDeque.remove();
            }
            // get the first robustness in deque
            Robustness found = lemireDeque.peekFirst();
            precomputed.add(new SimpleRobustness(found.getValue(), currentTime, consideredDimensions));
            currentIndex++;
            currentTime = current.next().getTime();
            if (memory != null) {
                lemireDeque.offer(memory);
                memory = null;
            }
        }
        return precomputed;
    }
}
