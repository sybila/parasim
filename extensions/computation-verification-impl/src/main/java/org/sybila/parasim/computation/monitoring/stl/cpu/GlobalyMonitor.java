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
package org.sybila.parasim.computation.monitoring.stl.cpu;

import java.util.List;
import java.util.LinkedList;
import java.util.Deque;
import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.stl.IntervalBoundaryType;
import org.sybila.parasim.model.verification.stl.TimeInterval;
import java.util.ListIterator;

/**
 * Monitors the robustness of Globaly_[interval] (sub) over a trajectory,
 * where sub is a subformula and interval is a time interval.
 *
 * Formaly the robustness value of G<sub>[a,b]</sub> (sub) in time t on trajectory
 * w is defined as
 *
 * Rob(G<sub>[a,b]</sub>sub,w,t) = min<sub>t' in [t+a, t+b]</sub>(Rob(sub,w,t'))
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @param <T> Type of trajectory over which the monitor functions.
 */
public class GlobalyMonitor<T extends Trajectory>
        implements Evaluable<T, SimplePropertyRobustness> {

    private Evaluable<T, PropertyRobustness> sub;
    private TimeInterval operatorInterval;

    public GlobalyMonitor(Evaluable<T, PropertyRobustness> sub, TimeInterval operatorInterval) {
        if (sub == null) {
            throw new IllegalArgumentException("Parameter sub is null.");
        }
        if (operatorInterval == null) {
            throw new IllegalArgumentException("Parameter operatorInterva is null.");
        }
        this.sub = sub;
        this.operatorInterval = operatorInterval;
    }

    /**
     * Evaluates the G<sub>I</sub> operator over the given <code>interval</code>
     * of the given PropertyRobustness <code>signal</code>.
     * Interval <code>I</code> = [a,b] is defined in the constructor of the monitor.
     *
     * The signal is expected to start at
     * interval.getLowerBound + operatorInterval.getLowerBound time units.
     *
     * @param signal Signal to process
     * @param interval Time interval over which to evaluate
     * @return List of values being the minimum over floating windows of time
     *         length b-a, starting at interval.getLowerBound + a.
     */
    protected List<SimplePropertyRobustness> evaluate(List<PropertyRobustness> signal,
            TimeInterval interval) {
        if (signal == null) {
            throw new IllegalArgumentException("Parameter signal is null.");
        }
        if (interval == null) {
            throw new IllegalArgumentException("Parameter interval is null.");
        }
        if (signal.isEmpty()) {
            throw new IllegalArgumentException("List signal is empty.");
        }

        ListIterator<PropertyRobustness> iterator = signal.listIterator();
        PropertyRobustness pr = iterator.next();

        if (pr.getTime() != interval.getLowerBound() + operatorInterval.getLowerBound()) {
            throw new IllegalArgumentException("The signal begining is incorrect.");
        }

        Deque<PropertyRobustness> maxWedge = new LinkedList<PropertyRobustness>();
        //PropertyRobustness localMax = new SimplePropertyRobustness();

        throw new RuntimeException("Not implemented.");
        //return null;
    }

    /**
     * Evaluates the G<sub>I</sub> operator over the given <code>interval</code>
     * of the given <code>trajectory</code>. Interval <code>I</code> = [a,b] is defined
     * in the constructor of the monitor.
     *
     * @param trajectory Trajectory over which to evaluate robustness.
     * @param interval The time interval over which to evaluate.
     * @return List of values being the minimum over floating windows of time
     *         length b-a, starting at interval.getLowerBound + a.
     */
    public List<SimplePropertyRobustness> evaluate(T trajectory, TimeInterval interval) {
        IntervalBoundaryType lowerType;
        if ((operatorInterval.getLowerBoundaryType() == IntervalBoundaryType.OPEN)
                || (interval.getLowerBoundaryType() == IntervalBoundaryType.OPEN)) {
            lowerType = IntervalBoundaryType.OPEN;
        } else {
            lowerType = IntervalBoundaryType.CLOSED;
        }

        IntervalBoundaryType upperType;
        if ((operatorInterval.getLowerBoundaryType() == IntervalBoundaryType.OPEN)
                || (interval.getLowerBoundaryType() == IntervalBoundaryType.OPEN)) {
            upperType = IntervalBoundaryType.OPEN;
        } else {
            upperType = IntervalBoundaryType.CLOSED;
        }


        TimeInterval evalInterval = new TimeInterval(operatorInterval.getLowerBound() + interval.getLowerBound(),
                operatorInterval.getUpperBound() + interval.getUpperBound(),
                lowerType, upperType);
        List<PropertyRobustness> signal = sub.evaluate(trajectory, evalInterval);
        return evaluate(signal, interval);
    }

    @Override
    public String toString() {
        return "G_" + operatorInterval.toString() + sub.toString();
    }
}
