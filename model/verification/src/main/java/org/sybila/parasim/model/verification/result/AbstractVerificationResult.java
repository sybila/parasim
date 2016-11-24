/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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

import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.space.OrthogonalSpaceImpl;
import org.sybila.parasim.model.trajectory.ArrayFractionPoint;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.FractionPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointWithNeigborhoodWrapper;
import org.sybila.parasim.model.trajectory.PointWithNeighborhood;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implements some auxiliary methods of {@link VerificationResult}
 * according with use of its interface methods.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class AbstractVerificationResult implements VerificationResult {

    private Robustness totalRobustness;

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractVerificationResult.class);

    /**
     * Return smallest space including all points comprising this result.
     * Linear to the number of points.
     *
     * Might be elevated into a factory in the future.
     *
     * @return Smallest space including all points.
     */
    public OrthogonalSpace getEncompassingSpace(OdeSystem odeSystem) {
       return getEncompassingSpace(this, odeSystem);
    }

    /**
     * Return smallest space including all points comprising a result.
     * Linear to the number of points.
     *
     * Might be elevated into a factory in the future.
     *
     * @param result Source result.
     *
     * @return Smallest space including all points.
     */
    public static OrthogonalSpace getEncompassingSpace(VerificationResult result, OdeSystem odeSystem) {
        if (result.size() == 0) {
            Point empty = new ArrayPoint(0, new float [0], 0, 0);
            return new OrthogonalSpaceImpl(empty, empty, odeSystem);
        }
        int dims = result.getPoint(0).getDimension();

        float [] mins = new float[dims];
        float [] maxs = new float[dims];

        for (int dim = 0; dim < dims; dim++) {
            mins[dim] = result.getPoint(0).getValue(dim);
            maxs[dim] = result.getPoint(0).getValue(dim);
        }

        for (int pointIndex = 1; pointIndex < result.size(); pointIndex++) {
            for (int dim = 0; dim < dims; dim++) {
                float val = result.getPoint(pointIndex).getValue(dim);
                if (val < mins[dim]) {
                    mins[dim] = val;
                } else if (val > maxs[dim]) {
                    maxs[dim] = val;
                }
            }
        }

        return new OrthogonalSpaceImpl(new ArrayPoint(0, mins, 0, dims), new ArrayPoint(0, maxs, 0, dims), odeSystem);
    }

    @Override
    public Element toXML(Document doc) {
        Element target = doc.createElement(VerificationResultFactory.RESULT_NAME);
        target.setAttribute(VerificationResultFactory.DIMENSION_NAME, (this.size() > 0 ? Integer.toString(this.getPoint(0).getDimension()) : Integer.toString(0)));
        target.setAttribute(VerificationResultFactory.ROBUSTNESS_NAME, Float.toString(getGlobalRobustness().getValue()));
        for (int i = 0; i < size(); i++) {
            target.appendChild(pointToXML(doc, getPoint(i), getRobustness(i)));
        }

        return target;
    }

    private Element pointToXML(Document doc, Point point, Robustness robustness) {

        Element target = doc.createElement(VerificationResultFactory.POINT_NAME);
        Element data = doc.createElement(VerificationResultFactory.DATA_NAME);
        target.appendChild(data);
        for (int i = 0; i < point.getDimension(); i++) {
            Element dim = doc.createElement(VerificationResultFactory.DIMENSION_NAME);
            dim.appendChild(doc.createTextNode(Float.toString(point.getValue(i))));
            data.appendChild(dim);
        }

        target.setAttribute(VerificationResultFactory.ROBUSTNESS_NAME, Float.toString(robustness.getValue()));

        return target;
    }

    @Override
    public boolean equals(Object obj) {
        //if (obj == this) return true;
        if (!(obj instanceof VerificationResult)) {
            return false;
        }
        VerificationResult target = (VerificationResult) obj;
        if (size() != target.size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!getPoint(i).equals(target.getPoint(i))) {
                return false;
            }
            if (Float.floatToIntBits(getRobustness(i).getValue()) != Float.floatToIntBits(target.getRobustness(i).getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 41;
        int result = size();
        for (int i = 0; i < size(); i++) {
            result *= prime;
            result += getPoint(i).hashCode();
            result *= prime;
            result += Float.floatToIntBits(getRobustness(i).getValue());
        }
        return result;
    }

    @Override
    public VerificationResult merge(VerificationResult toMerge) {
        if (toMerge == null) {
            throw new IllegalArgumentException("The parameter [toMerge] is null.");
        }
        long duplicates =0;
        // Map with point as a key is performence problem, but it's the only way
        // to remove duplicities
        Map<FractionPoint, Pair<PointWithNeighborhood, Robustness>> robustnesses = new TreeMap<>();
        // copy this data
        for (int i=0; i<size(); i++) {
            PointWithNeigborhoodWrapper wrapper= (PointWithNeigborhoodWrapper) getPoint(i);
            FractionPoint key = ((ArrayFractionPoint) wrapper.unwrap()).getFractionPoint();
            robustnesses.put(key, new Pair<>(getPoint(i), getRobustness(i)));
        }
        // copy other data
        for (int i=0; i<toMerge.size(); i++) {
            PointWithNeigborhoodWrapper wrapper= (PointWithNeigborhoodWrapper) toMerge.getPoint(i);
            FractionPoint key = ((ArrayFractionPoint) wrapper.unwrap()).getFractionPoint();
            if (!robustnesses.containsKey(key)) {
                robustnesses.put(key, new Pair<>(toMerge.getPoint(i), toMerge.getRobustness(i)));
            } else {
                duplicates++;
            }
        }
        PointWithNeighborhood[] newPoints = new PointWithNeighborhood[robustnesses.size()];
        Robustness[] newRobustnesses = new Robustness[robustnesses.size()];
        int index = 0;
        for (Pair<PointWithNeighborhood, Robustness> entry : robustnesses.values()) {
            newPoints[index] = entry.first();
            newRobustnesses[index] = entry.second();
            index++;
        }
        LOGGER.info("result merging: <{}> duplicates", duplicates);
        return new ArrayVerificationResult(newPoints, newRobustnesses);
    }

    protected void setGlobalRobustness(Robustness robustness) {
        this.totalRobustness = robustness;
    }

    @Override
    public Robustness getGlobalRobustness() {
        if (this.totalRobustness == null) {
            this.totalRobustness = calculateGlobalRobustness();
        }
        return this.totalRobustness;
    }

    protected Robustness calculateGlobalRobustness() {
        return Robustness.UNDEFINED;
    }

    protected FractionPoint getFractionPoint(int i) {
        Point point = getPoint(i);
        if (point instanceof PointWithNeighborhood) {
            return ((ArrayFractionPoint) ((PointWithNeighborhood) point).unwrap()).getFractionPoint();
        } else {
            return ((ArrayFractionPoint) point).getFractionPoint();
        }
    }

}
