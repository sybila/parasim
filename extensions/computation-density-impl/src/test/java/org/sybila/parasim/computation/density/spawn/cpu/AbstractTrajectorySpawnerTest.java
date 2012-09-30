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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.sybila.parasim.model.trajectory.Point;
import java.util.Set;
import java.util.HashSet;
import org.sybila.parasim.computation.density.AbstractDensityTest;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.model.ode.SimpleOdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpaceImpl;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhood;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractTrajectorySpawnerTest extends AbstractDensityTest {

    protected static final int DIMENSION = 4;
    protected static final int TO_SPAWN = 3;

    protected void testInitialSpawn() {
        Collection<OdeSystemVariable> vars = new ArrayList<>();
        int index = 0;
        for (String name: new String[] {"x", "y", "z"}) {
            vars.add(new OdeSystemVariable(name, index, new Constant(index)));
            index++;
        }
        OrthogonalSpaceImpl space = new OrthogonalSpaceImpl(
            new ArrayPoint(0, 0, 0, 0),
            new ArrayPoint(0, 3 * (TO_SPAWN - 1), 3 * (TO_SPAWN - 1), 3 * (TO_SPAWN - 1)),
            new SimpleOdeSystem(vars, Collections.EMPTY_LIST, Collections.EMPTY_LIST)
        );
        DataBlock<TrajectoryWithNeighborhood> spawned = initialSpawn(space, TO_SPAWN);
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
        assertEquals(spawned.size(), (int) Math.pow(TO_SPAWN, DIMENSION));
    }

    protected void testDistanceOfTrajectoriesAfterInitialSpawn()     {
        SpawnedDataBlock spawned = initialSpawn(createOrthogonalSpace((TO_SPAWN - 1) * 4.0f, DIMENSION), TO_SPAWN);
        for (TrajectoryWithNeighborhood trajectory: spawned) {
            for (Trajectory neighbor: trajectory.getNeighbors()) {
                boolean distanceMatches = false;
                for(int dim=0; dim<trajectory.getDimension(); dim++) {
                    if ((float) Math.abs(trajectory.getFirstPoint().getValue(dim) - neighbor.getFirstPoint().getValue(dim)) == (dim + 1) * 2.0f) {
                        distanceMatches = true;
                    }
                }
                assertTrue(
                    distanceMatches,
                    "Distance of seed " + trajectory.getFirstPoint() + " and its neighbor " + neighbor.getFirstPoint() + " doesn't match,"
                );
            }
        }
    }

    protected void testNumberOfTrajectoriesInNeighborhoodAfterInitialSpawn() {
        SpawnedDataBlock spawned = initialSpawn(createOrthogonalSpace((float) 7.82, DIMENSION), TO_SPAWN);
        for (TrajectoryWithNeighborhood trajectory : spawned) {
            assertTrue(trajectory.getNeighbors().size() <= 2 * DIMENSION, "The number of trajectories in neigborhood has to be lower or equal to 2 * dimension <" + DIMENSION +">, but it is <" + trajectory.getNeighbors().size() +">.");
        }
    }

    protected OrthogonalSpaceImpl createOrthogonalSpace(float base, int dimension) {
        float[] minBounds = new float[dimension];
        float[] maxBounds = new float[dimension];
        Collection<OdeSystemVariable> vars = new ArrayList<>();
        for (int dim=0; dim<dimension; dim++) {
            minBounds[dim] = 0;
            maxBounds[dim] = (float) ((dim+1) * base);
            vars.add(new OdeSystemVariable("x" + dim, dim, new Constant(dim)));
        }
        return new OrthogonalSpaceImpl(
            new ArrayPoint(0, minBounds),
            new ArrayPoint(100, maxBounds),
            new SimpleOdeSystem(vars, Collections.EMPTY_LIST, Collections.EMPTY_LIST)
        );
    }

    protected SpawnedDataBlock initialSpawn(OrthogonalSpaceImpl space, int numSpawn) {
        TrajectorySpawner spawner = createTrajectorySpawner();
        return spawner.spawn(
            space,
            createInitialSampling(space, numSpawn)
        );
    }

    abstract protected TrajectorySpawner createTrajectorySpawner();

}
