package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointDerivative;
import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;

/**
 * Represents a predicate evaluation function which gives the quantitative
 * semantics of a predicate in a point together with it's derivatives.
 *
 * To obtain the point's derivatives either the next point must be given to
 * be able to compute the difference or the point must be extended with derivative
 * values (PointDerivative interface) or the derivatives must be given explicitly.
 *
 * The function is expected to be linear in the coordinates of point p so that
 * it's derivative is a constant.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PredicateEvaluator<R extends PropertyRobustness> {

    R value(Point p, Point next);

    R value(PointDerivative p);
}
