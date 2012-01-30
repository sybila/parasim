
package org.sybila.parasim.computation.density.spawn.cpu;

import org.sybila.parasim.computation.MapTrajectoryNeighborhood;
import org.sybila.parasim.computation.density.AbstractDensityTest;
import org.sybila.parasim.computation.density.spawn.TrajectorySpawner;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractTrajectorySpawnerTest extends AbstractDensityTest {
    
    protected static final int DIMENSION = 4;
    protected static final int TO_SPAWN = 3;
    
    protected void testInitialSpawn() {
        TrajectorySpawner spawner = createTrajectorySpawner();
        int[] toSpawn = new int[DIMENSION];
        float[] minBounds = new float[DIMENSION];
        float[] maxBounds = new float[DIMENSION];
        for (int dim=0; dim<DIMENSION; dim++) {
            toSpawn[dim] = TO_SPAWN;
            minBounds[dim] = 0;
            maxBounds[dim] = (float) ((dim+1) * 7.82);
        }
        DataBlock<Trajectory> spawned = spawner.spawn(
            createConfiguration((float) 1.0, DIMENSION , new MapTrajectoryNeighborhood<Trajectory>()),
            new OrthogonalSpace(
                new ArrayPoint(minBounds, 0),
                new ArrayPoint(maxBounds, 100)
            ),
            toSpawn
        );
        assertEquals(spawned.size(), (int) Math.pow(TO_SPAWN, DIMENSION));
    }
    
    abstract protected TrajectorySpawner createTrajectorySpawner();
    
}
