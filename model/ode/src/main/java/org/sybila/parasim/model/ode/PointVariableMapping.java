package org.sybila.parasim.model.ode;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Maps variable names to dimensions of {@link Point}.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public interface PointVariableMapping extends VariableMapping<Integer> {
    
    /**
     * @return Dimension of given {@link Point}.
     */
    public int getDimension();
}
