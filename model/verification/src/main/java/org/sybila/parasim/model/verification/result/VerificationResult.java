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
package org.sybila.parasim.model.verification.result;

import org.sybila.parasim.model.Mergeable;
import org.sybila.parasim.model.trajectory.PointWithNeighborhood;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.xml.XMLRepresentable;

/**
 * Result of a verification -- list of points with associated property robustness value.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface VerificationResult extends XMLRepresentable, Mergeable<VerificationResult> {

    /**
     * Return number of points with robustness values.
     * @return Number of points.
     */
    int size();

    /**
     * Return coordinates of a given point.
     * @param index Index of specified point.
     * @return Coordinates of point with specified index.
     */
    PointWithNeighborhood getPoint(int index);

    /**
     * Return property robustness value in a specified point.
     * @param index Index of specified point.
     * @return Property robustness value in point with given index.
     */
    Robustness getRobustness(int index);

    /**
     * Return the robustness summarized for all points.
     */
    Robustness getGlobalRobustness();

}
