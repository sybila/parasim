package org.sybila.parasim.computation.lifecycle;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class AbstractComputationController implements ComputationController {

    private Resources speed;
    
    protected final SimpleComputationStatus status = new SimpleComputationStatus();
    
    public Resources getSpeed() {
        return speed;
    }

    public ComputationStatus getStatus() {
        return status;
    }

    public void setSpeed(Resources speed) {
        if (speed == null) {
            throw new IllegalArgumentException("The parameter [speed] is null.");
        }
        this.speed = speed;
    }

}