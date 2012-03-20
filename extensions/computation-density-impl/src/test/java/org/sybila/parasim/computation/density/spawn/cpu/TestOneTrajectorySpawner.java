package org.sybila.parasim.computation.density.spawn.cpu;

import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.distancecheck.cpu.OnePairDistanceChecker;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryNeighborhood;


/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestOneTrajectorySpawner extends AbstractTrajectorySpawnerTest {
 
    @Test
    @Override
    public void testInitialSpawn() {
        super.testInitialSpawn();
    }
    
    @Test
    public void testSimple() {
        DataBlock<Trajectory> dataBlock = createDataBlock(2, 4, 4, 2, (float) 0.1, (float) 0.01);
        TrajectoryNeighborhood<Trajectory> neighborhood = createNeighborhood(dataBlock);
        Configuration<Trajectory> configuration = createConfiguration(1, 4, neighborhood);
        DistanceCheckedDataBlock<Trajectory> distanceChecked = new OnePairDistanceChecker().check(configuration, dataBlock);
        SpawnedDataBlock<Trajectory> spawned = new OneTrajectorySpawner().spawn(configuration, distanceChecked);
        assertEquals(1, spawned.size());
        assertEquals(2, spawned.getNeighborhood().getNeighbors(spawned.getTrajectory(0)).size());
    }
    
    @Test
    @Override
    public void testDistanceOfTrajectoriesAfterInitialSpawn() {
        super.testDistanceOfTrajectoriesAfterInitialSpawn();
    }
    
    @Test
    @Override
    public void testNumberOfTrajectoriesAfterInitialSpawn() {
        super.testNumberOfTrajectoriesAfterInitialSpawn();
    }
    
    @Test
    @Override
    public void testNumberOfTrajectoriesInNeighborhoodAfterInitialSpawn() {
        super.testNumberOfTrajectoriesInNeighborhoodAfterInitialSpawn();
    }
    
    @Override
    protected TrajectorySpawner createTrajectorySpawner() {
        return new OneTrajectorySpawner();
    }    
    
}