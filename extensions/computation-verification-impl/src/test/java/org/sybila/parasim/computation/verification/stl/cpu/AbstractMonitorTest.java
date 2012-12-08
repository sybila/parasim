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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.sybila.parasim.computation.verification.cpu.AbstractMonitor;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.Signal;
import org.sybila.parasim.model.verification.SimpleRobustness;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class AbstractMonitorTest {

    protected static final Property EMPTY_PROPERTY = new Property() {
        @Override
        public float getTimeNeeded() {
            return 0;
        }

        @Override
        public List<Signal> getSignals() {
            return Collections.EMPTY_LIST;
        }
    };

    protected Monitor createTestMonitor(final float... values) {
        return new AbstractMonitor(EMPTY_PROPERTY) {
            @Override
            public Robustness getRobustness(int index) {
                return new SimpleRobustness(values[index], index);
            }
            @Override
            public int size() {
                return values.length;
            }
            @Override
            public Collection<Monitor> getSubmonitors() {
                return Collections.EMPTY_LIST;
            }
        };
    }

    protected Monitor createDecreasingTestMonitor(final int size, final float factor) {
        final List<Robustness> robustnesses = new ArrayList<>();
        for (int i=size-1; i>=0; i--) {
            robustnesses.add(new SimpleRobustness(i == 0 ? 0 : i * factor, 9 - i));
        }
        return new AbstractMonitor(EMPTY_PROPERTY) {
            @Override
            public Robustness getRobustness(int index) {
                return robustnesses.get(index);
            }
            @Override
            public int size() {
                return size;
            }
            @Override
            public Collection<Monitor> getSubmonitors() {
                return Collections.EMPTY_LIST;
            }

        };
    }

    protected Monitor createIncreasingTestMonitor(final int size, final float factor) {
        final List<Robustness> robustnesses = new ArrayList<>();
        for (int i=0; i<size; i++) {
            robustnesses.add(new SimpleRobustness(i == 0 ? 0 : i * factor, i));
        }
        return new AbstractMonitor(EMPTY_PROPERTY) {
            @Override
            public Robustness getRobustness(int index) {
                return robustnesses.get(index);
            }
            @Override
            public int size() {
                return size;
            }
            @Override
            public Collection<Monitor> getSubmonitors() {
                return Collections.EMPTY_LIST;
            }

        };
    }

}
