package org.sybila.parasim.computation.density.distancecheck;

import org.sybila.parasim.computation.density.Distance;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleDistance implements Distance {

    private float[] distances;
    private float maxDistance;
    
    public SimpleDistance(float[] distances, float maxDistance) {
        if (distances == null) {
            throw new IllegalArgumentException("The parameter distances is null.");
        }
        this.distances = distances;
        this.maxDistance = maxDistance;
    }
    
    @Override
    public float getMaxDistance() {
        return maxDistance;
    }

    @Override
    public float getDimensionDistance(int dimensionIndex) {
        return distances[dimensionIndex];
    }
    
}
