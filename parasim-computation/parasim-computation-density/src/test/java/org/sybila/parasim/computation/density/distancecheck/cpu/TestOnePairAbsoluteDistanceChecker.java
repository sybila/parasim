package org.sybila.parasim.computation.density.distancecheck.cpu;

import org.junit.Test;
import org.sybila.parasim.computation.TrajectoryNeighborhood;
import static org.junit.Assert.*;
import org.sybila.parasim.computation.density.AbstractDensityTest;
import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestOnePairAbsoluteDistanceChecker extends AbstractDensityTest {

    @Test
    public void testInvalid() {
        DataBlock<Trajectory> dataBlock = createDataBlock(4, 4, 4, 2, (float) 0.1, (float) 0.01);
        TrajectoryNeighborhood<Trajectory> neighborhood = createNeighborhood(dataBlock);
        Configuration<Trajectory> configuration = createConfiguration(1, 4, neighborhood);
        DistanceCheckedDataBlock<Trajectory> result = new OnePairDistanceChecker().check(configuration, dataBlock);
        for (int t = 0; t < result.size(); t++) {
            for (int dim = 0; dim < result.getDistances(t).size(); dim++) {
                assertTrue(!result.getDistances(t).get(dim).isValid());
            }
        }
    }
    
    @Test
    public void testValid() {
        DataBlock<Trajectory> dataBlock = createDataBlock(4, 4, 4, (float) 0.1, (float) 0.01, (float) 0.001);
        TrajectoryNeighborhood<Trajectory> neighborhood = createNeighborhood(dataBlock);
        Configuration<Trajectory> configuration = createConfiguration(1, 4, neighborhood);
        DistanceCheckedDataBlock<Trajectory> result = new OnePairDistanceChecker().check(configuration, dataBlock);
        for (int t = 0; t < result.size(); t++) {
            for (int dim = 0; dim < result.getDistances(t).size(); dim++) {
                assertTrue(result.getDistances(t).get(dim).isValid());
            }
        }
    }
}
