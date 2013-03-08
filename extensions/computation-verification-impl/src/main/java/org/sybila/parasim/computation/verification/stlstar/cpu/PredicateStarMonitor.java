package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.stl.Predicate;
import org.sybila.parasim.model.verification.stlstar.ArrayMultiPoint;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;
import org.sybila.parasim.model.verification.stlstar.MultiPoint;
import org.sybila.parasim.util.Coordinate;

/**
 * Monitor for a predicate with freeze-time value semantics.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PredicateStarMonitor extends AbstractStarMonitor {

    private final Trajectory trajectory;
    private final Predicate predicate;

    public PredicateStarMonitor(Predicate predicate, Trajectory trajectory, FormulaStarInfo info) {
        super(predicate, info);
        Validate.notNull(trajectory);
        this.trajectory = trajectory;
        this.predicate = predicate;
    }

    private MultiPoint createMultiPoint(Coordinate index) {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i <= predicate.getStarNumber(); i++) {
            points.add(trajectory.getPoint(index.getCoordinate(i)));
        }
        return new ArrayMultiPoint(points);
    }

    @Override
    protected Robustness computeRobustness(Coordinate index) {
        MultiPoint mp = createMultiPoint(index);
        return new SimpleRobustness(predicate.getValue(mp), mp.getPoint(0).getTime(), getProperty());
    }

    @Override
    public Collection<Monitor> getSubmonitors() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public int size() {
        return trajectory.getLength();
    }
}