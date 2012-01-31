package org.sybila.parasim.support.model.ode;

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class BayramovInitialSpace extends OrthogonalSpace {

    
    public BayramovInitialSpace() {
        super(
            new ArrayPoint(0, (float) 0.0015, (float) 0.00095, (float) 0.00038),
            new ArrayPoint(0, (float) 0.0040, (float) 0.00405, (float) 0.00482)
        );
    }
    
}
