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
package org.sybila.parasim.computation.verification.cpu;

import org.sybila.parasim.computation.verification.api.Monitor;
import java.util.Iterator;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractMonitor implements Monitor {

    private final Property property;

    public AbstractMonitor(Property property) {
        Validate.notNull(property);
        this.property = property;
    }

    @Override
    public Property getProperty() {
        return property;
    }

    @Override
    public Iterator<Robustness> iterator() {
        return new Iterator<Robustness>() {
            private int next = 0;
            @Override
            public boolean hasNext() {
                return next < size();
            }
            @Override
            public Robustness next() {
                return getRobustness(next++);
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

}
