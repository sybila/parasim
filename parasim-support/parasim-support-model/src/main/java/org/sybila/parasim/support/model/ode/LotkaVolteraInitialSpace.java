package org.sybila.parasim.support.model.ode;

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LotkaVolteraInitialSpace extends OrthogonalSpace {

    
    public LotkaVolteraInitialSpace() {
        super(
            new ArrayPoint(0, 1, 1),
            new ArrayPoint(0, 10, 10)
        );
    }
    
}
