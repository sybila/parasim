package org.sybila.parasim.computation.cycledetection.cpu;

import org.sybila.parasim.computation.cycledetection.api.CycleDetectorFactory;
import org.sybila.parasim.model.trajectory.PointComparator;

/**
 * A factory to create Brent's cycle detectors.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class BrentsCycleDetectorFactory implements CycleDetectorFactory<BrentsCycleDetector> {

    private PointComparator comparator;

    public BrentsCycleDetectorFactory(PointComparator comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("The parameter comparator is null.");
        }
        this.comparator = comparator;
    }

    @Override
    public BrentsCycleDetector create() {
        return new BrentsCycleDetector(comparator);
    }

    /**
     * @return the comparator
     */
    public PointComparator getComparator() {
        return comparator;
    }

    /**
     * @param comparator the comparator to set
     */
    public void setComparator(PointComparator comparator) {
        this.comparator = comparator;
    }
}
