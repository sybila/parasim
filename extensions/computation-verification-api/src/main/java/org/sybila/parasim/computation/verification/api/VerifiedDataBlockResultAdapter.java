/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.computation.verification.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointWithNeigborhoodWrapper;
import org.sybila.parasim.model.trajectory.PointWithNeighborhood;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhood;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.result.AbstractVerificationResult;

/**
 * Adapter from {@link VerifiedDataBlock} to {@link org.sybila.parasim.model.verification.result.VerificationResult}.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class VerifiedDataBlockResultAdapter<T extends Trajectory> extends AbstractVerificationResult {

    private PointWithNeighborhood[] points;
    private Robustness[] robustnesses;

    public VerifiedDataBlockResultAdapter(VerifiedDataBlock<T> data) {
        int index = 0;
        points = new PointWithNeighborhood[data.size()];
        robustnesses = new Robustness[data.size()];
        for (Trajectory t: data) {
            if (t instanceof TrajectoryWithNeighborhood && ((TrajectoryWithNeighborhood) t).getNeighbors().size() > 0) {
                Collection<Point> neighbors = new ArrayList<>();
                for (Trajectory n: ((TrajectoryWithNeighborhood) t).getNeighbors()) {
                    neighbors.add(n.getFirstPoint());
                }
                PointWithNeighborhood point = new PointWithNeigborhoodWrapper(t.getFirstPoint(), neighbors);
                points[index] = point;
            } else {
                points[index] = new PointWithNeigborhoodWrapper(t.getFirstPoint(), Collections.EMPTY_LIST);
            }
            robustnesses[index] = data.getRobustness(index);
            index++;
        }
    }

    @Override
    public int size() {
        return points.length;
    }

    @Override
    public PointWithNeighborhood getPoint(int index) {
        return points[index];
    }

    @Override
    public Robustness getRobustness(int index) {
        return robustnesses[index];
    }
}
