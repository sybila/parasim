package org.sybila.parasim.computation.verification.api;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Property;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Verifier<P extends Property> {

    VerifiedDataBlock<Trajectory> verify(DataBlock<Trajectory> trajectories, P property);

}
