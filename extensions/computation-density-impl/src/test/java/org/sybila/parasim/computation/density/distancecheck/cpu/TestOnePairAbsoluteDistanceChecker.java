/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.computation.density.distancecheck.cpu;

import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.sybila.parasim.computation.density.AbstractDensityTest;

import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryNeighborhood;

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
        Configuration configuration = createConfiguration(1, DIMENSION, neighborhood);
        DistanceCheckedDataBlock result = new OnePairDistanceChecker().check(configuration, dataBlock);
        for (int t = 0; t < result.size(); t++) {
            for (int neigh = 0; neigh < neighborhood.getNeighbors(dataBlock.getTrajectory(t)).size(); neigh++) {
                assertEquals(result.getTrajectoryCheckedPosition(t, neigh), LENGTH - 1);
                assertEquals(result.getNeighborCheckedPosition(t, neigh), LENGTH - 1);
            }
        }
    }
    
    @Test
    public void testInvalidDistance() {
        DataBlock<Trajectory> dataBlock = createInvalidDataBlock();
        TrajectoryNeighborhood<Trajectory> neighborhood = createNeighborhood(dataBlock);
        Configuration configuration = createConfiguration(1, DIMENSION, neighborhood);
        DistanceCheckedDataBlock result = new OnePairDistanceChecker().check(configuration, dataBlock);
        for (int t = 0; t < result.size(); t++) {
            for (int dim = 0; dim < neighborhood.getNeighbors(dataBlock.getTrajectory(t)).size(); dim++) {
                assertTrue(!result.getDistance(t, dim).isValid());
            }
        }
    }
    
    @Test
    public void testValidDistance() {
        DataBlock<Trajectory> dataBlock = createValidDataBlock();
        TrajectoryNeighborhood<Trajectory> neighborhood = createNeighborhood(dataBlock);
        Configuration configuration = createConfiguration(1, DIMENSION, neighborhood);
        DistanceCheckedDataBlock result = new OnePairDistanceChecker().check(configuration, dataBlock);
        for (int t = 0; t < result.size(); t++) {
            for (int dim = 0; dim < neighborhood.getNeighbors(dataBlock.getTrajectory(t)).size(); dim++) {
                assertTrue(result.getDistance(t, dim).isValid());
            }
        }
    }
    
    private DataBlock<Trajectory> createInvalidDataBlock() {
        return createDataBlock(SIZE, LENGTH, DIMENSION, 2, (float) 0.1, (float) 0.01);
    }
    
    private DataBlock<Trajectory> createValidDataBlock() {
        return createDataBlock(SIZE, LENGTH, DIMENSION, (float) 0.1, (float) 0.01, (float) 0.001);
    }
}
