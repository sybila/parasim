package org.sybila.parasim.computation.cycledetection;

import org.sybila.parasim.computation.Module;
import org.sybila.parasim.computation.ModuleComputationException;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Enables cycle detection on datablocks of trajectories given a specific
 * type of cycle detector and it's factory.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class CycleDetectionModule<CD extends CycleDetector,
                                  CDF extends CycleDetectorFactory<CD>> implements Module<DataBlock, CycleDetectDataBlock<Trajectory>> {

    private Detector<CD, CDF, CycleDetectDataBlock<Trajectory>> detector;
    private CDF factory;

    public CycleDetectionModule(Detector<CD, CDF, CycleDetectDataBlock<Trajectory>> detector, CDF factory)
    {
        if (detector == null) {
            throw new IllegalArgumentException("The parameter detector is null.");
        }        
        if (factory == null) {
            throw new IllegalArgumentException("The parameter factory is null.");
        }
        this.detector = detector;
        this.factory = factory;
    }

    @Override
    public CycleDetectDataBlock compute(DataBlock input) throws ModuleComputationException
    {
        return compute(input, 0);
    }

    public CycleDetectDataBlock compute(DataBlock input, int stepLimit) throws ModuleComputationException
    {
        return detector.detect(factory, input, stepLimit);
    }

    public CycleDetectDataBlock compute(DataBlock input, CD[] detectors) throws ModuleComputationException
    {        
        return compute(input, detectors, 0);
    }

    public CycleDetectDataBlock compute(DataBlock input, CD[] detectors, int stepLimit) throws ModuleComputationException
    {
        if (input.size() != detectors.length)
        {
            throw new IllegalArgumentException("Number of trajectories in parameter [input] must match number of [detector]s.");
        }
        return detector.detect(input, detectors, stepLimit);
    }

    public CycleDetectDataBlock compute(DataBlock oldInput, CD[] oldDetectors, DataBlock newInput, int stepLimit) throws ModuleComputationException
    {
        if (oldInput.size() != oldDetectors.length)
        {
            throw new IllegalArgumentException("Number of trajectories in [oldInput] must match number of [oldDetector]s.");
        }
        return detector.detect(factory, oldInput, oldDetectors, newInput, stepLimit);
    }

    /**
     * Returns the CycleDetectionFactory used to create CycleDetectors.
     *
     * @return CycleDetectorFactory.
     */
    public CycleDetectorFactory getComparator()
    {
        return factory;
    }

    /**
     * Sets the CycleDetectionFactory used to create CycleDetectors.
     *
     * @param factory The new CycleDetectorFactory.
     */
    public void setComparator(CDF factory) {
        if (factory == null)
        {
            throw new IllegalArgumentException("The parameter comparator is null.");
        }
        this.factory = factory;
    }

}
