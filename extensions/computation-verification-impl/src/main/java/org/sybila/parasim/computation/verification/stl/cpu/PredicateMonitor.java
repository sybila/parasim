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

import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.cpu.AbstractMonitor;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.stl.Predicate;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class PredicateMonitor extends AbstractMonitor {

    private final Predicate predicate;
    private final Trajectory trajectory;

    public PredicateMonitor(Trajectory trajectory, Predicate predicate) {
        Validate.notNull(trajectory);
        Validate.notNull(predicate);
        this.predicate = predicate;
        this.trajectory = trajectory;
    }

    public Robustness getRobustness(int index) {
        return new SimpleRobustness(predicate.getValue(trajectory.getPoint(index)), trajectory.getPoint(index).getTime());
    }

    public int size() {
        return trajectory.getLength();
    }

}
