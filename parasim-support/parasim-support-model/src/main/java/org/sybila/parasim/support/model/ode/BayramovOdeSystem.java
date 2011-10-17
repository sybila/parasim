package org.sybila.parasim.support.model.ode;

import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class BayramovOdeSystem implements OdeSystem {

    @Override
    public int dimension() {
        return 3;
    }

    @Override
    public float value(Point point, int dimension) {
        switch(dimension) {
            case 0:
                return (float) (0.0005 - 250 * point.getValue(0) * point.getValue(1));
            case 1:
                return (float) (0.00001 - 0.1 * point.getValue(1) - 250 * point.getValue(0) * point.getValue(1) + 300 * point.getValue(1) * point.getValue(2));
            case 2:
                return (float) (250 * point.getValue(0) * point.getValue(1) - 300 * point.getValue(1) * point.getValue(2));
            default:
                throw new IndexOutOfBoundsException("The dimension is out of the range [0,2].");
        }
    }

    @Override
    public float value(float[] point, int dimension) {
        switch(dimension) {
            case 0:
                return (float) (0.0005 - 250 * point[0] * point[1]);
            case 1:
                return (float) (0.00001 - 0.1 * point[1] - 250 * point[0] * point[1] + 300 * point[1] * point[2]);
            case 2:
                return (float) (250 * point[0] * point[1] - 300 * point[1] * point[2]);
            default:
                throw new IndexOutOfBoundsException("The dimension is out of the range [0,2].");
        }
    }
    
}
