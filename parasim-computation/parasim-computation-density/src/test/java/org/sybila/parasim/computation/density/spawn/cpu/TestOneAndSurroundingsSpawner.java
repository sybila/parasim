package org.sybila.parasim.computation.density.spawn.cpu;

import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.distancecheck.DistanceChecker;
import org.sybila.parasim.computation.density.distancecheck.cpu.OnePairDistanceChecker;
import org.sybila.parasim.computation.density.spawn.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.TrajectorySpawner;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestOneAndSurroundingsSpawner extends AbstractTrajectorySpawnerTest {

    @Test
    public void testNumberOfTrajectoriesAfterSpawn() {
        // initial sampling
        SpawnedDataBlock<Trajectory> initSpawned = initialSpawn(createOrthogonalSpace(4.0f * (4 - 1), 2), 4);
        // load configuration
        Configuration configuration = createConfiguration(1.5f, DIMENSION, initSpawned.getNeighborhood());
        // distance checking
        DistanceChecker distanceChecker = new OnePairDistanceChecker();
        DistanceCheckedDataBlock distanceChecked = distanceChecker.check(configuration, initSpawned);
        // assertions in distances
        for (int t=0; t<distanceChecked.size(); t++) {
            Trajectory trajectory = initSpawned.getTrajectory(t);
            for (int n=0; n<initSpawned.getNeighborhood().getNeighbors(trajectory).size(); n++) {
                assertFalse(distanceChecked.getDistance(t, n).isValid(), "Validity of distance of trajectories [" + t + ", " + n + "] doesn't match. Distance is [" + distanceChecked.getDistance(t, n).value() + "].");
            }
        }
        // create spawner
        TrajectorySpawner spawner = createTrajectorySpawner();
        // continue in sampling
        SpawnedDataBlock<Trajectory> nextSpawned = spawner.spawn(configuration, distanceChecked);
        // assertion
        int expectedSpawned = 0;
        for (Trajectory trajectory: initSpawned) {
            expectedSpawned += initSpawned.getNeighborhood().getNeighbors(trajectory).size();
        }
        assertEquals(nextSpawned.size(), expectedSpawned);
    }
    
    @Override
    protected TrajectorySpawner createTrajectorySpawner() {
        return new OneAndSurroundingsTrajectorySpawner();
    }

}