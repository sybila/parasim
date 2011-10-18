package org.sybila.parasim.support.model.ode;

import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LotkaVolteraOdeSystem implements OdeSystem {

    @Override
    public int dimension() {
        return 2;
    }

    @Override
    public float value(Point point, int dimension) {
        switch(dimension) {
            case 0:
                return (float) (10.1 * point.getValue(0) - point.getValue(0) * point.getValue(1));
            case 1:
                return (float) (point.getValue(0) * point.getValue(1) - 5.4 * point.getValue(1));
            default:
                throw new IndexOutOfBoundsException("The dimension is out of the range [0,1].");
        }
    }

    @Override
    public float value(float[] point, int dimension) {
        switch(dimension) {
            case 0:
                return (float) (10.1 * point[0] - point[0] * point[1]);
            case 1:
                return (float) (point[0] * point[1] - 5.4 * point[1]);
            default:
                throw new IndexOutOfBoundsException("The dimension is out of the range [0,1].");
        }
    }

    public String octaveName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String octaveString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
