package org.sybila.parasim.computation.cycledetection;

import org.sybila.parasim.computation.Module;
import org.sybila.parasim.computation.ModuleComputationException;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.PointComparator;

/**
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public class CycleDetectionModule implements Module<DataBlock, CycleDetectDataBlock<Trajectory>> {

    private Detector<PointComparator, CycleDetector, CycleDetectDataBlock<Trajectory>> detector;
    private PointComparator comparator;

    public CycleDetectionModule(Detector<PointComparator, CycleDetector, CycleDetectDataBlock<Trajectory>> detector,
            PointComparator comparator)
    {
        if (detector == null) {
            throw new IllegalArgumentException("The parameter detector is null.");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("The parameter comparator is null.");
        }
        this.detector = detector;
        this.comparator = comparator;
    }

    @Override
    public CycleDetectDataBlock compute(DataBlock input) throws ModuleComputationException
    {
        return detector.detect(comparator, input, 0);
    }

    public CycleDetectDataBlock compute(DataBlock input, int stepLimit) throws ModuleComputationException
    {
        return detector.detect(comparator, input, stepLimit);
    }

    public CycleDetectDataBlock compute(CycleDetectDataBlock input) throws ModuleComputationException
    {
        return detector.detect(comparator, input, 0);
    }

    public CycleDetectDataBlock compute(CycleDetectDataBlock input, int stepLimit) throws ModuleComputationException
    {
        return detector.detect(comparator, input, stepLimit);
    }

    /**
     * Returns the comparator used for cycle detection
     *
     * @return comparator
     */
    public PointComparator getComparator() {
        return comparator;
    }

    /**
     * Sets a comparator used for cycle detection
     *
     * @param comparator
     */
    public void setComparator(PointComparator comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("The parameter comparator is null.");
        }
        this.comparator = comparator;
    }

}