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
package org.sybila.parasim.computation.density.spawn.cpu;

import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceChecker;
import org.sybila.parasim.computation.density.distancecheck.cpu.OnePairDistanceChecker;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.model.trajectory.Trajectory;
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
        SpawnedDataBlock initSpawned = initialSpawn(createOrthogonalSpace(4.0f * (4 - 1), 2), 4);
        // load configuration
        Configuration configuration = createConfiguration(
                createPointDistanceMetric(1.5f, DIMENSION),
                initSpawned.getConfiguration().getInitialSampling(),
                initSpawned.getConfiguration().getInitialSpace(),
                initSpawned.getConfiguration().getNeighborhood());
        // distance checking
        DistanceChecker distanceChecker = new OnePairDistanceChecker();
        DistanceCheckedDataBlock distanceChecked = distanceChecker.check(configuration, initSpawned);
        // assertions in distances
        for (int t=0; t<distanceChecked.size(); t++) {
            Trajectory trajectory = initSpawned.getTrajectory(t);
            for (int n=0; n<initSpawned.getConfiguration().getNeighborhood().getNeighbors(trajectory).size(); n++) {
                assertFalse(distanceChecked.getDistance(t, n).isValid(), "Validity of distance of trajectories [" + t + ", " + n + "] doesn't match. Distance is [" + distanceChecked.getDistance(t, n).value() + "].");
            }
        }
        // create spawner
        TrajectorySpawner spawner = createTrajectorySpawner();
        // continue in sampling
        SpawnedDataBlock nextSpawned = spawner.spawn(configuration, distanceChecked);
        // assertion
        int expectedSpawned = 0;
        for (Trajectory trajectory: initSpawned) {
            expectedSpawned += initSpawned.getConfiguration().getNeighborhood().getNeighbors(trajectory).size();
        }
        assertEquals(nextSpawned.size(), expectedSpawned);
    }

    @Override
    protected TrajectorySpawner createTrajectorySpawner() {
        return new OneAndSurroundingsTrajectorySpawner();
    }

}
