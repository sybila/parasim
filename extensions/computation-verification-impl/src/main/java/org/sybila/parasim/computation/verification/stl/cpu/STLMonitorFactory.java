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

import java.util.Collection;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.api.MonitorFactory;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FutureFormula;
import org.sybila.parasim.model.verification.stl.GloballyFormula;
import org.sybila.parasim.model.verification.stl.Predicate;
import org.sybila.parasim.model.verification.stl.UntilFormula;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class STLMonitorFactory implements MonitorFactory<Formula> {

    private final boolean filterDimensions;

    public STLMonitorFactory(boolean filterDimensions) {
        this.filterDimensions = filterDimensions;
    }

    @Override
    public Monitor createMonitor(Trajectory trajectory, Formula property) {
        if (trajectory.getLastPoint().getTime() < property.getTimeNeeded()) {
            throw new IllegalArgumentException("The formula " + property + " needs the trajectory simulated at least to time " + property.getTimeNeeded() + ", " + trajectory.getLastPoint().getTime() + " given.");
        }
        Collection<Integer> consideredDimensions = null;
        if (filterDimensions) {
            consideredDimensions = property.getVariableIndexes();
        }
        switch(property.getType()) {
            case AND:
                return new AndMonitor(property, createMonitor(trajectory, property.getSubformula(0)), createMonitor(trajectory, property.getSubformula(1)), consideredDimensions);
            case FUTURE:
                return new FutureMonitor(property, createMonitor(trajectory, property.getSubformula(0)), ((FutureFormula) property).getInterval(), consideredDimensions);
            case GLOBALLY:
                return new GloballyMonitor(property, createMonitor(trajectory, property.getSubformula(0)), ((GloballyFormula) property).getInterval(), consideredDimensions);
            case NOT:
                return new NotMonitor(property, createMonitor(trajectory, property.getSubformula(0)));
            case OR:
                return new OrMonitor(property, createMonitor(trajectory, property.getSubformula(0)), createMonitor(trajectory, property.getSubformula(1)), consideredDimensions);
            case PREDICATE:
                return new PredicateMonitor(property, trajectory, (Predicate) property, consideredDimensions);
            case UNTIL:
                return new UntilMonitor(property, createMonitor(trajectory, property.getSubformula(0)), createMonitor(trajectory, property.getSubformula(1)), ((UntilFormula) property).getInterval(), consideredDimensions);
            default:
                throw new UnsupportedOperationException("There is no available monitor for formula type [" + property.getType() + "].");
        }
    }

}
