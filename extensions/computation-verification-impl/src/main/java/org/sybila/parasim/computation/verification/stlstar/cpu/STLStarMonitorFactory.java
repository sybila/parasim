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

import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.api.MonitorFactory;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FreezeFormula;
import org.sybila.parasim.model.verification.stl.Predicate;
import org.sybila.parasim.model.verification.stl.TemporalFormula;
import org.sybila.parasim.model.verification.stl.UntilFormula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.model.verification.stlstar.StarMerger;

/**
 * Transforms a STL formula into monitor which monitors its "starred" version
 * (i.e. takes frozen-time values into account). Call the {@link #createMonitor}
 * method only on top-level formula. Otherwise, use {@link #createStarMonitor}.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum STLStarMonitorFactory implements MonitorFactory<Formula> {

    /**
     * Singleton instance.
     */
    INSTANCE;

    @Override
    public Monitor createMonitor(Trajectory trajectory, Formula property) {
        Formula simplified = StarMerger.mergeStars(property);
        return createStarMonitor(trajectory, simplified, new FormulaStarInfo(simplified));
    }

    /**
     * Creates a monitor which monitors formula over given trajectory for STS
     * semantics with frozen-time values.
     *
     * @param trajectory Trajectory to monitor over.
     * @param property Property to monitor.
     * @return A monitor using frozen-time semantics.
     */
    public StarMonitor createStarMonitor(Trajectory trajectory, Formula property, FormulaStarInfo info) {
        switch (property.getType()) {
            case PREDICATE:
                return new PredicateStarMonitor((Predicate) property, trajectory, info);
            case NOT:
                return new NotStarMonitor(property, info, createStarMonitor(trajectory, property.getSubformula(0), info));
            case AND:
                return new AndStarMonitor(property, info, createStarMonitor(trajectory, property.getSubformula(0), info),
                        createStarMonitor(trajectory, property.getSubformula(1), info));
            case OR:
                return new OrStarMonitor(property, info, createStarMonitor(trajectory, property.getSubformula(0), info),
                        createStarMonitor(trajectory, property.getSubformula(1), info));
            case FREEZE:
                return new FreezeMonitor((FreezeFormula) property, info, createStarMonitor(trajectory, property.getSubformula(0), info));
            case FUTURE:
                return UnaryTemporalStarMonitor.getFutureMonitor((TemporalFormula) property, info,
                        createStarMonitor(trajectory, property.getSubformula(0), info));
            case GLOBALLY:
                return UnaryTemporalStarMonitor.getGloballyMonitor((TemporalFormula) property, info,
                        createStarMonitor(trajectory, property.getSubformula(0), info));
            case UNTIL:
                return new UntilStarMonitor((UntilFormula) property, info,
                        createStarMonitor(trajectory, property.getSubformula(0), info),
                        createStarMonitor(trajectory, property.getSubformula(1), info));
            default:
                throw new UnsupportedOperationException("There is no available monitor for formula type [" + property.getType() + "].");
        }
    }

    /**
     * Return an instance of this factory.
     *
     * @return A monitor factory.
     */
    public static MonitorFactory<Formula> getInstance() {
        return INSTANCE;
    }
}
