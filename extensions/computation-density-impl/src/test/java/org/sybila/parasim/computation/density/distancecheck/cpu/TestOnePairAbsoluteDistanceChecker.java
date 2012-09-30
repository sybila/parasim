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

import java.util.Iterator;
import org.sybila.parasim.model.trajectory.LimitedPointDistanceMetric;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.sybila.parasim.computation.density.AbstractDensityTest;

import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.api.DistanceMetricDataBlock;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhood;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestOnePairAbsoluteDistanceChecker extends AbstractDensityTest {

    private static final int DIMENSION = 4;
    private static final int LENGTH = 4;
    private static final int SIZE = 4;

    @Test
    public void testValidCheckedLengths() {
        final DataBlock<TrajectoryWithNeighborhood> dataBlock = createValidDataBlock();
        Configuration configuration = createConfiguration(
                createInitialSampling(createInitialSpace(1, DIMENSION),DIMENSION),
                createInitialSpace(1, DIMENSION));
        final LimitedPointDistanceMetric distanceMetric = createPointDistanceMetric(1, DIMENSION);
        DistanceCheckedDataBlock result = new OnePairDistanceChecker().check(
                configuration,
                new DistanceMetricDataBlock<TrajectoryWithNeighborhood>() {
                    @Override
                    public LimitedPointDistanceMetric getDistanceMetric(int index) {
                        return distanceMetric;
                    }
                    @Override
                    public TrajectoryWithNeighborhood getTrajectory(int index) {
                        return dataBlock.getTrajectory(index);
                    }
                    @Override
                    public int size() {
                        return dataBlock.size();
                    }
                    @Override
                    public Iterator<TrajectoryWithNeighborhood> iterator() {
                        return dataBlock.iterator();
                    }
                });
        for (int t = 0; t < result.size(); t++) {
            for (int neigh = 0; neigh < dataBlock.getTrajectory(t).getNeighbors().size(); neigh++) {
                assertEquals(result.getTrajectoryCheckedPosition(t, neigh), LENGTH - 1);
                assertEquals(result.getNeighborCheckedPosition(t, neigh), LENGTH - 1);
            }
        }
    }

    @Test
    public void testInvalidDistance() {
        final DataBlock<TrajectoryWithNeighborhood> dataBlock = createInvalidDataBlock();
        final LimitedPointDistanceMetric distanceMetric = createPointDistanceMetric(1, DIMENSION);
        Configuration configuration = createConfiguration(
                createInitialSampling(createInitialSpace(1, DIMENSION),DIMENSION),
                createInitialSpace(1, DIMENSION));
        DistanceCheckedDataBlock result = new OnePairDistanceChecker().check(
                configuration,
                new DistanceMetricDataBlock<TrajectoryWithNeighborhood>() {
                    @Override
                    public LimitedPointDistanceMetric getDistanceMetric(int index) {
                        return distanceMetric;
                    }
                    @Override
                    public TrajectoryWithNeighborhood getTrajectory(int index) {
                        return dataBlock.getTrajectory(index);
                    }
                    @Override
                    public int size() {
                        return dataBlock.size();
                    }
                    @Override
                    public Iterator<TrajectoryWithNeighborhood> iterator() {
                        return dataBlock.iterator();
                    }
                });
        for (int t = 0; t < result.size(); t++) {
            for (int dim = 0; dim < dataBlock.getTrajectory(t).getNeighbors().size(); dim++) {
                assertTrue(!result.getDistance(t, dim).isValid());
            }
        }
    }

    @Test
    public void testValidDistance() {
        final DataBlock<TrajectoryWithNeighborhood> dataBlock = createValidDataBlock();
        final LimitedPointDistanceMetric distanceMetric = createPointDistanceMetric(1, DIMENSION);
        Configuration configuration = createConfiguration(
                createInitialSampling(createInitialSpace(1, DIMENSION),DIMENSION),
                createInitialSpace(1, DIMENSION));
        DistanceCheckedDataBlock result = new OnePairDistanceChecker().check(
                configuration,
                new DistanceMetricDataBlock<TrajectoryWithNeighborhood>() {
                    @Override
                    public LimitedPointDistanceMetric getDistanceMetric(int index) {
                        return distanceMetric;
                    }
                    @Override
                    public TrajectoryWithNeighborhood getTrajectory(int index) {
                        return dataBlock.getTrajectory(index);
                    }
                    @Override
                    public int size() {
                        return dataBlock.size();
                    }
                    @Override
                    public Iterator<TrajectoryWithNeighborhood> iterator() {
                        return dataBlock.iterator();
                    }
                });
        for (int t = 0; t < result.size(); t++) {
            for (int dim = 0; dim < dataBlock.getTrajectory(t).getNeighbors().size(); dim++) {
                assertTrue(result.getDistance(t, dim).isValid());
            }
        }
    }

    private DataBlock<TrajectoryWithNeighborhood> createInvalidDataBlock() {
        return createDataBlock(SIZE, LENGTH, DIMENSION, 2, (float) 0.1, (float) 0.01);
    }

    private DataBlock<TrajectoryWithNeighborhood> createValidDataBlock() {
        return createDataBlock(SIZE, LENGTH, DIMENSION, (float) 0.1, (float) 0.01, (float) 0.001);
    }
}
