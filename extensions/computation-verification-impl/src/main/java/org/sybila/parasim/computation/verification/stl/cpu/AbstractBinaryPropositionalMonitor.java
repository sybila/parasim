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

import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.cpu.AbstractMonitor;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractBinaryPropositionalMonitor extends AbstractMonitor {

    private final Monitor left;
    private final Monitor right;
    private final Property property;

    public AbstractBinaryPropositionalMonitor(Property property, Monitor left, Monitor right) {
        super(property);
        Validate.notNull(left);
        Validate.notNull(right);
        this.left = left;
        this.right = right;
        this.property = property;
    }

    @Override
    public Robustness getRobustness(int index) {
        return evaluate(left.getRobustness(index), right.getRobustness(index), property);
    }

    @Override
    public Collection<Monitor> getSubmonitors() {
        return Arrays.asList(left, right);
    }

    @Override
    public int size() {
        return Math.min(left.size(), right.size());
    }

    protected abstract Robustness evaluate(Robustness left, Robustness right, Property property);

}
