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

import org.sybila.parasim.model.trajectory.Point;
import java.util.Set;
import java.util.HashSet;
import org.sybila.parasim.computation.density.AbstractDensityTest;
import org.sybila.parasim.computation.density.api.ArrayInitialSampling;
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
            new ArrayPoint(0, 0, 0, 0),
            new ArrayPoint(0, 3 * (TO_SPAWN - 1), 3 * (TO_SPAWN - 1), 3 * (TO_SPAWN - 1))
        );
        DataBlock<Trajectory> spawned = initialSpawn(space, TO_SPAWN);
        Set<Point> expected = new HashSet<Point>();
        for (int x=0; x<3 * TO_SPAWN; x+=3) {
            for(int y=0; y<3 * TO_SPAWN; y+=3) {
                for (int z=0; z<3 * TO_SPAWN; z+=3) {
                    expected.add(new ArrayPoint(0, x, y, z));
                }
            }
        }
        for (Trajectory trajectory : spawned) {
            assertTrue(expected.contains(trajectory.getFirstPoint()), "There is unexpected seed " + trajectory.getFirstPoint() + ".");
        }
    }

    protected void testNumberOfTrajectoriesAfterInitialSpawn() {
        SpawnedDataBlock spawned = initialSpawn(createOrthogonalSpace((float) 7.82, DIMENSION), TO_SPAWN);
        assertEquals(spawned.size() + spawned.getSecondaryTrajectories().size(), (int) Math.pow(TO_SPAWN, DIMENSION));
    }

    protected void testDistanceOfTrajectoriesAfterInitialSpawn()     {
        SpawnedDataBlock spawned = initialSpawn(createOrthogonalSpace((TO_SPAWN - 1) * 4.0f, DIMENSION), TO_SPAWN);
        for (Trajectory trajectory: spawned) {
            for (Trajectory neighbor: spawned.getConfiguration().getNeighborhood().getNeighbors(trajectory)) {
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
        SpawnedDataBlock spawned = initialSpawn(createOrthogonalSpace((float) 7.82, DIMENSION), TO_SPAWN);
        for (Trajectory trajectory : spawned) {
            assertTrue(spawned.getConfiguration().getNeighborhood().getNeighbors(trajectory).size() <= 2 * DIMENSION, "The number of trajectories in neigborhood has to be lower or equal to 2 * dimension <" + DIMENSION +">, but it is <" + spawned.getConfiguration().getNeighborhood().getNeighbors(trajectory).size() +">.");
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

    protected SpawnedDataBlock initialSpawn(OrthogonalSpace space, int numSpawn) {
        TrajectorySpawner spawner = createTrajectorySpawner();
        int[] toSpawn = new int[space.getDimension()];
        for (int dim=0; dim<space.getDimension(); dim++) {
            toSpawn[dim] = numSpawn;
        }
        return spawner.spawn(
            space,
            new ArrayInitialSampling(toSpawn)
        );
    }

    abstract protected TrajectorySpawner createTrajectorySpawner();

}
