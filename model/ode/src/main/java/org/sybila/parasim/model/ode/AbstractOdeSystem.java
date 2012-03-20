package org.sybila.parasim.model.ode;

import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractOdeSystem implements OdeSystem {
    
    public float value(Point point, int dimension) {
        if (dimension < 0 || dimension >= dimension()) {
            throw new IndexOutOfBoundsException("The dimension is out of the range [0, " + (dimension() - 1) + "].");
        }
        float result = 0;
        for(int c = 0; c < encoding().countCoefficients(dimension); c++) {
            float subResult = encoding().coefficient(dimension, c);
            for(int f = 0; f < encoding().countFactors(dimension, c); f++) {
                subResult *= point.getValue(encoding().factor(dimension, c, f));
            }
            result += subResult;
        }
        return result;
    }

    public float value(float[] point, int dimension) {
        if (dimension < 0 || dimension >= dimension()) {
            throw new IndexOutOfBoundsException("The dimension is out of the range [0, " + (dimension() - 1) + "].");
        }
        float result = 0;
        for(int c = 0; c < encoding().countCoefficients(dimension); c++) {
            float subResult = encoding().coefficient(dimension, c);
            for(int f = 0; f < encoding().countFactors(dimension, c); f++) {
                subResult *= point[encoding().factor(dimension, c, f)];
            }
            result += subResult;
        }
        return result;
    }    
    
}
