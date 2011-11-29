package org.sybila.parasim.computation.monitoring.cpu;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.computation.monitoring.PropertyRobustness;

/**
 * Represents a predicate evaluation function which gives the quantitative
 * semantics of a predicate in a point together with it's derivatives.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface PredicateEvaluator<R extends PropertyRobustness>
{
    R value(Point p);
}
