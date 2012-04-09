package org.sybila.parasim.model.verification.stl;

/**
 * Represents a temporal formula with a specified time interval for which
 * is should be evaluated.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface TemporalFormula extends Formula {

    FormulaInterval getInterval();
}
