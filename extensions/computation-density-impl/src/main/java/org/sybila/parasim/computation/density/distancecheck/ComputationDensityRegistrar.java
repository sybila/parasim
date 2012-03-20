package org.sybila.parasim.computation.density.distancecheck;

import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceChecker;
import org.sybila.parasim.computation.density.distancecheck.cpu.OnePairDistanceChecker;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.computation.density.spawn.cpu.OneAndSurroundingsTrajectorySpawner;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ComputationDensityRegistrar {
    
    @Inject
    private Instance<DistanceChecker> distanceChecker;
    @Inject
    private Instance<TrajectorySpawner> spawner;
    
    public void registerDistanceChecker(@Observes ParasimDescriptor descriptor) {
        distanceChecker.set(new OnePairDistanceChecker());
        spawner.set(new OneAndSurroundingsTrajectorySpawner());
    }
    
}
