package org.sybila.parasim.computation.verification.stlstar.cpu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.cpu.AbstractMonitor;
import org.sybila.parasim.computation.verification.cpu.StarMonitor;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.stl.Predicate;
import org.sybila.parasim.model.verification.stlstar.ArrayMultiPoint;
import org.sybila.parasim.util.Coordinate;

/**
 * Monitor for a predicate with freeze-time value semantics.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class StarPredicateMonitor extends AbstractMonitor implements StarMonitor {

    private final Trajectory trajectory;
    private final Predicate predicate;

    public StarPredicateMonitor(Property property, Trajectory trajectory, Predicate predicate) {
        super(property);
        Validate.notNull(trajectory);
        Validate.notNull(predicate);
        this.trajectory = trajectory;
        this.predicate = predicate;
    }

    @Override
    public Robustness getRobustness(Coordinate index) {
        if (index.getDimension() <= predicate.getStarNumber()) {
            throw new IllegalArgumentException("Coordinate is too short to be evaluated.");
        }
        List<Point> points = new ArrayList<>();
        for (int i = 0; i <= predicate.getStarNumber(); i++) {
            points.add(trajectory.getPoint(index.getCoordinate(i)));
        }
        return new SimpleRobustness(predicate.getValue(new ArrayMultiPoint(points)), points.get(0).getTime(), getProperty());
    }

    @Override
    public Robustness getRobustness(int index) {
        int freeze = predicate.getStarNumber();
        Coordinate.Builder coord = new Coordinate.Builder(freeze + 1);
        coord.setCoordinate(0, index);
        return getRobustness(coord.create());
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