
package org.sybila.parasim.computation.density.spawn.cpu;

import org.sybila.parasim.model.trajectory.Point;
import java.util.Set;
import java.util.HashSet;
import org.sybila.parasim.computation.density.AbstractDensityTest;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
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
        OrthogonalSpace space = new OrthogonalSpace(
            new ArrayPoint(0, 0, 0),
            new ArrayPoint(0, 3 * (TO_SPAWN - 1), 3 * (TO_SPAWN - 1))
        );
        DataBlock<Trajectory> spawned = initialSpawn(space, TO_SPAWN);
        Set<Point> expected = new HashSet<Point>();
        for (int x=0; x<3 * TO_SPAWN; x+=3) {
            for(int y=0; y<3 * TO_SPAWN; y+=3) {
                expected.add(new ArrayPoint(0, x, y));
            }
        }
        for (Trajectory trajectory : spawned) {
            assertTrue(expected.contains(trajectory.getFirstPoint()), "There is unexpected seed " + trajectory.getFirstPoint() + ".");
        }
    }    

    protected void testNumberOfTrajectoriesAfterInitialSpawn() {
        SpawnedDataBlock<Trajectory> spawned = initialSpawn(createOrthogonalSpace((float) 7.82, DIMENSION), TO_SPAWN);
        assertEquals(spawned.size() + spawned.getSecondaryTrajectories().size(), (int) Math.pow(TO_SPAWN, DIMENSION));
    }
    
    protected void testDistanceOfTrajectoriesAfterInitialSpawn()     {
        SpawnedDataBlock<Trajectory> spawned = initialSpawn(createOrthogonalSpace((TO_SPAWN - 1) * 4.0f, DIMENSION), TO_SPAWN);
        for (Trajectory trajectory: spawned) {
            for (Trajectory neighbor: spawned.getNeighborhood().getNeighbors(trajectory)) {
                boolean distanceMatches = false;
                for(int dim=0; dim<trajectory.getDimension(); dim++) {
                    if ((float) Math.abs(trajectory.getFirstPoint().getValue(dim) - neighbor.getFirstPoint().getValue(dim)) == (dim + 1) * 4.0f) {
                        distanceMatches = true;
                    }
                }
                assertTrue(
                    distanceMatches,
                    "Distance of seed " + trajectory.getFirstPoint() + " and its neighbor " + neighbor.getFirstPoint() + " doesn't match."
                );
            }
        }
    }
    
    protected void testNumberOfTrajectoriesInNeighborhoodAfterInitialSpawn() {
        SpawnedDataBlock<Trajectory> spawned = initialSpawn(createOrthogonalSpace((float) 7.82, DIMENSION), TO_SPAWN);
        for (Trajectory trajectory : spawned) {
            assertTrue(spawned.getNeighborhood().getNeighbors(trajectory).size() <= 2 * DIMENSION, "The number of trajectories in neigborhood has to be lower or equal to 2 * dimension <" + DIMENSION +">, but it is <" + spawned.getNeighborhood().getNeighbors(trajectory).size() +">.");
        }
    }
    
    protected OrthogonalSpace createOrthogonalSpace(float base, int dimension) {
        float[] minBounds = new float[dimension];
        float[] maxBounds = new float[dimension];
        for (int dim=0; dim<dimension; dim++) {
            minBounds[dim] = 0;
            maxBounds[dim] = (float) ((dim+1) * base);
        }
        return new OrthogonalSpace(
            new ArrayPoint(0, minBounds),
            new ArrayPoint(100, maxBounds)
        );
    }
    
    protected SpawnedDataBlock<Trajectory> initialSpawn(OrthogonalSpace space, int numSpawn) {
        TrajectorySpawner spawner = createTrajectorySpawner();
        int[] toSpawn = new int[space.getDimension()];
        for (int dim=0; dim<space.getDimension(); dim++) {
            toSpawn[dim] = numSpawn;
        }
        return spawner.spawn(
            space,
            toSpawn
        );
    }
        
    abstract protected TrajectorySpawner createTrajectorySpawner();
    
}
