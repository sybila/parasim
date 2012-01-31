package org.sybila.parasim.support.computation.simulation;

import org.sybila.parasim.computation.simulation.ImmutableConfiguration;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.support.model.ode.BayramovOdeSystem;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class BayramovConfiguration extends ImmutableConfiguration {
    
    public BayramovConfiguration() {
        super(
            new BayramovOdeSystem(),
            new OrthogonalSpace(
                new ArrayPoint(0, 0, 0, 0),
                new ArrayPoint(1000, 200, 200, 200)                   
            ),
            new float[] {1, 1, 1},
            (float) 0.1,
            100000000
        );
    }
    
}
