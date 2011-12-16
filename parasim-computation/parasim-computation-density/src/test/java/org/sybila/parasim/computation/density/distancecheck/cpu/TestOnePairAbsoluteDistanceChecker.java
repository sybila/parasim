package org.sybila.parasim.computation.density.distancecheck.cpu;

import org.testng.annotations.Test;
import org.sybila.parasim.computation.TrajectoryNeighborhood;
import static org.testng.Assert.*;
import org.sybila.parasim.computation.density.AbstractDensityTest;
import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestOnePairAbsoluteDistanceChecker extends AbstractDensityTest {

    private static final int DIMENSION = 4;
    private static final int LENGTH = 4;
    private static final int SIZE = 4;
    
    @Test
    public void testValidCheckedLengths() {
        DataBlock<Trajectory> dataBlock = createValidDataBlock();
        TrajectoryNeighborhood<Trajectory> neighborhood = createNeighborhood(dataBlock);
        Configuration<Trajectory> configuration = createConfiguration(1, DIMENSION, neighborhood);
        DistanceCheckedDataBlock<Trajectory> result = new OnePairDistanceChecker().check(configuration, dataBlock);
        for (int t = 0; t < result.size(); t++) {
            for (int neigh = 0; neigh < neighborhood.getNeighbors(dataBlock.getTrajectory(t)).size(); neigh++) {
                assertEquals(result.getTrajectoryCheckedPosition(t, neigh), LENGTH - 1);
                assertEquals(result.getNeighborCheckedPosition(t, neigh), LENGTH - 1);
            }
        }
    }
    
//    @Test
//    public void testInvalidDistance() {
//        DataBlock<Trajectory> dataBlock = createInvalidDataBlock();
//        TrajectoryNeighborhood<Trajectory> neighborhood = createNeighborhood(dataBlock);
//        Configuration<Trajectory> configuration = createConfiguration(1, DIMENSION, neighborhood);
//        DistanceCheckedDataBlock<Trajectory> result = new OnePairDistanceChecker().check(configuration, dataBlock);
//        for (int t = 0; t < result.size(); t++) {
//            for (int dim = 0; dim < neighborhood.getNeighbors(dataBlock.getTrajectory(t)).size(); dim++) {
//                assertTrue(!result.getDistance(t, dim).isValid());
//            }
//        }
//    }
//    
//    @Test
//    public void testValidDistance() {
//        DataBlock<Trajectory> dataBlock = createValidDataBlock();
//        TrajectoryNeighborhood<Trajectory> neighborhood = createNeighborhood(dataBlock);
//        Configuration<Trajectory> configuration = createConfiguration(1, DIMENSION, neighborhood);
//        DistanceCheckedDataBlock<Trajectory> result = new OnePairDistanceChecker().check(configuration, dataBlock);
//        for (int t = 0; t < result.size(); t++) {
//            for (int dim = 0; dim < neighborhood.getNeighbors(dataBlock.getTrajectory(t)).size(); dim++) {
//                assertTrue(result.getDistance(t, dim).isValid());
//            }
//        }
//    }
    
    private DataBlock<Trajectory> createInvalidDataBlock() {
        return createDataBlock(SIZE, LENGTH, DIMENSION, 2, (float) 0.1, (float) 0.01);
    }
    
    private DataBlock<Trajectory> createValidDataBlock() {
        return createDataBlock(SIZE, LENGTH, DIMENSION, (float) 0.1, (float) 0.01, (float) 0.001);
    }
}
