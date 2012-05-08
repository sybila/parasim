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

import org.sybila.parasim.computation.verification.cpu.Monitor;
import org.sybila.parasim.computation.verification.cpu.MonitorFactory;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FutureFormula;
import org.sybila.parasim.model.verification.stl.GloballyFormula;
import org.sybila.parasim.model.verification.stl.Predicate;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class STLMonitorFactory implements MonitorFactory<Formula> {

    public Monitor createMonitor(Trajectory trajectory, Formula property) {
        if (trajectory.getLastPoint().getTime() < property.getTimeNeeded()) {
            throw new IllegalArgumentException("The formula needs the trajectory simulated at least to time " + property.getTimeNeeded() + ", " + trajectory.getLastPoint().getTime() + " given.");
        }
        switch(property.getType()) {
            case AND:
                return new AndMonitor(createMonitor(trajectory, property.getSubformula(0)), createMonitor(trajectory, property.getSubformula(1)));
            case FUTURE:
                return new FutureMonitor(createMonitor(trajectory, property.getSubformula(0)), ((FutureFormula) property).getInterval());
            case GLOBALLY:
                return new GloballyMonitor(createMonitor(trajectory, property.getSubformula(0)), ((GloballyFormula) property).getInterval());
            case NOT:
                return new NotMonitor(createMonitor(trajectory, property.getSubformula(0)));
            case OR:
                return new OrMonitor(createMonitor(trajectory, property.getSubformula(0)), createMonitor(trajectory, property.getSubformula(1)));
            case PREDICATE:
                return new PredicateMonitor(trajectory, (Predicate) property);
            default:
                throw new UnsupportedOperationException("There is no available monitor for formula type [" + property.getType() + "].");
        }
    }

}
