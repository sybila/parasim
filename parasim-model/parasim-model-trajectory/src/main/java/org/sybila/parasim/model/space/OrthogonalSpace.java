package org.sybila.parasim.model.space;

import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OrthogonalSpace implements Space {

    private Point maxBounds;
    private Point minBounds; 
    
    public OrthogonalSpace(Point minBounds, Point maxBounds) {
        if (minBounds == null) {
            throw new IllegalArgumentException("The parameter [minBounds] is null.");
        }
        if (maxBounds == null) {
            throw new IllegalArgumentException("The parameter [maxBounds] is null.");
        }        
        if (minBounds.getDimension() != maxBounds.getDimension()) {
            throw new IllegalArgumentException("The dimension of [minBounds] and [maxBounds] doesn't match.");
        }
        for (int i=0; i < minBounds.getDimension(); i++) {
            if (minBounds.getValue(i) > maxBounds.getValue(i)) {
                throw new IllegalArgumentException("The min bound in dimension <" + i + "> is greater than max bound.");
            }
        }
        this.minBounds = minBounds;
        this.maxBounds = maxBounds;
    }

    @Override
    public int getDimension() {
        return maxBounds.getDimension();
    }

    public Point getMaxBounds() {
        return maxBounds;
    }
    
    public Point getMinBounds() {
        return minBounds;
    }
    
    @Override
    public float getSize(int dimension) {
        return maxBounds.getValue(dimension) - minBounds.getValue(dimension);
    }

    @Override
    public boolean isIn(Point point) {
        if (point.getDimension() != getDimension()) {
            throw new IllegalArgumentException("The given point has different dimension than the space.");
        }
        for (int dim=0; dim<getDimension(); dim++) {
            if (point.getValue(dim) < minBounds.getValue(dim)) {
                return false;
            }
            if (point.getValue(dim) > maxBounds.getValue(dim)) {
                return false;
            }
        }
        return true;
    }
    
    
    public boolean isIn(float[] point) {
        if (point.length != getDimension()) {
            throw new IllegalArgumentException("The given point has different dimension than the space.");
        }
        for (int dim=0; dim<getDimension(); dim++) {
            if (point[dim] < minBounds.getValue(dim)) {
                return false;
            }
            if (point[dim] > maxBounds.getValue(dim)) {
                return false;
            }
        }
        return true;
    }
}
