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
        Configuration configuration = createConfiguration(1, 4, neighborhood);
        DistanceCheckedDataBlock distanceChecked = new OnePairDistanceChecker().check(configuration, dataBlock);
        SpawnedDataBlock spawned = new OneTrajectorySpawner().spawn(configuration, distanceChecked);
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