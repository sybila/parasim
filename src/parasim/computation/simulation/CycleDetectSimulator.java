
package parasim.computation.simulation;

import java.util.List;

/**
 * Extends the dense batch simulator with cycle detection.
 * 
 * @author sven
 */
public interface CycleDetectSimulator extends DenseSimulator {         

    /**
     * @param detectors List of CycleDetectors belonging to trajectories to be
     *        simulated. WARNING The detectors are modified during the
     *        the call to the simulate method.
     */
    void setCycleDetectors(List<CycleDetector> detectors);

    /**
     * @return A list of modified CycleDetectors belonging to simulated
     *         trajectories.
     */
    List<CycleDetector> getCycleDetectors();
    
}
