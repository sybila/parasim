
package org.sybila.parasim.computation.density.spawn.cpu;

import org.sybila.parasim.computation.MapTrajectoryNeighborhood;
import org.sybila.parasim.computation.density.AbstractDensityTest;
import org.sybila.parasim.computation.density.spawn.SpawnedDataBlock;
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
    
    protected void testNumberOfTrajectoriesAfterInitialSpawn() {
        DataBlock<Trajectory> spawned = initialSpawn();
        assertEquals(spawned.size(), (int) Math.pow(TO_SPAWN, DIMENSION));
    }
    
    protected void testNumberOfTrajectoriesInNeighborhoodAfterInitialSpawn() {
        SpawnedDataBlock<Trajectory> spawned = initialSpawn();
        for (Trajectory trajectory : spawned) {
            assertTrue(spawned.getNeighborhood().getNeighbors(trajectory).size() <= 4, "The number of trajectories in neigborhood has to be lower or equal to dimension <" + DIMENSION +">, but it is <" + spawned.getNeighborhood().getNeighbors(trajectory).size() +">.");
        }
    }
    
    private SpawnedDataBlock<Trajectory> initialSpawn() {
        TrajectorySpawner spawner = createTrajectorySpawner();
        int[] toSpawn = new int[DIMENSION];
        float[] minBounds = new float[DIMENSION];
        float[] maxBounds = new float[DIMENSION];
        for (int dim=0; dim<DIMENSION; dim++) {
            toSpawn[dim] = TO_SPAWN;
            minBounds[dim] = 0;
            maxBounds[dim] = (float) ((dim+1) * 7.82);
        }
        return spawner.spawn(
            createConfiguration((float) 1.0, DIMENSION , new MapTrajectoryNeighborhood<Trajectory>()),
            new OrthogonalSpace(
                new ArrayPoint(0, minBounds),
                new ArrayPoint(100, maxBounds)
            ),
            toSpawn
        );
    }
    
    abstract protected TrajectorySpawner createTrajectorySpawner();
    
}
