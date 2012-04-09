package org.sybila.parasim.computation.cycledetection.api;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.Iterator;
import java.util.Arrays;

/**
 * Detects cycles on data blocks of trajectories using cycle detectors created
 * by the given CycleDetectorFactory.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public class GeneralDetector<CD extends CycleDetector, CDF extends CycleDetectorFactory<CD>, T extends Trajectory>
        implements Detector<CD, CDF, CycleDetectDataBlock<T, CD>> {

    /**
     * Detects cycles on given trajectories using the cycle detectors created
     * by the given cycle detection factory with a maximum
     * of stepLimit points processed on every trajectory.
     *
     * @param factory CycleDetectionFactory to create cycle detectors.
     * @param trajectories Set of trajectories on which to detect cycles.
     * @param stepLimit Maximum number of points to process from each trajectory.
     * @return Cycle detection data block with results of the computation.
     */
    @Override
    public CycleDetectDataBlock detect(CDF factory,
            DataBlock<Trajectory> trajectories, int stepLimit) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        if (trajectories.size() <= 0) {
            throw new IllegalArgumentException("Trajectories must contain at least one trajectory.");
        }
        CycleDetector[] cycleDetectors = new CycleDetector[trajectories.size()];
        CycleDetectionStatus[] statuses = new CycleDetectionStatus[trajectories.size()];
        Arrays.fill(statuses, CycleDetectionStatus.COMPUTING);
        for (int i = 0; i < trajectories.size(); i++) {
            cycleDetectors[i] = factory.create();
        }
        CycleDetectDataBlock<T, CD> dataBlock = new ArrayCycleDetectionDataBlock(trajectories, cycleDetectors, statuses);
        return this.detect(dataBlock, stepLimit);
    }

    /**
     * Resumes cycle detection on given trajectories using the cycle detectors
     * already initialized in the cycle detection data block. A maximum
     * of stepLimit points is processed on every trajectory.
     *
     * @param trajectories Set of trajectories on which to detect cycles.
     * @param stepLimit Maximum number of points to process from each trajectory.
     * @return Cycle detection data block with results of the computation.
     */
    @Override
    public CycleDetectDataBlock<T, CD> detect(CycleDetectDataBlock<T, CD> trajectories, int stepLimit) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        if (trajectories.size() <= 0) {
            throw new IllegalArgumentException("Trajectories must contain at least one trajectory.");
        }
        if (stepLimit < 0) {
            throw new IllegalArgumentException("The parameter stepLimit must be none negative.");
        }

        Iterator<T> tIterator = trajectories.iterator();
        int index = 0;
        while (tIterator.hasNext()) {
            Trajectory t = tIterator.next();
            CD detector = trajectories.getCycleDetector(index);
            int stepsUsed = detector.detectCycle(t, stepLimit);
            if (detector.cycleDetected()) {
                trajectories.setStatus(index, CycleDetectionStatus.CYCLE);
            } else if (stepLimit == 0 || stepLimit > stepsUsed) {
                trajectories.setStatus(index, CycleDetectionStatus.NOCYCLE);
            }
            index++;
        }
        return trajectories;
    }
    /**
     * Detects cycles on two blocks of trajectories merging the results.
     * The first set given by oldTrajectories is expected to have already
     * some cycle detection performed on them and the array oldDetectors belongs
     * to these. The second set given by newTrajectories is expected to have
     * no cycle detection yet performed.
     *
     * @param comparator Point comparator for similarity tests between points.
     * @param oldTrajectories Set of partialy processed trajectories.
     * @param oldDetectors Array of cycle detectors of all oldTrajectories.
     * @param newTrajectories Set of new trajectories on which to detect cycles.
     * @param stepLimit Maximum number of points to process from each trajectory.
     * @return Cycle detection data block with results of the computation for both
     *         DataBlocks, old first then new ones.
     */
    /*    public ArrayCycleDetectionDataBlock detect(CDF factory,
    DataBlock<Trajectory> oldTrajectories,
    CD[] oldDetectors,
    DataBlock<Trajectory> newTrajectories,
    int stepLimit)
    {
    if (oldTrajectories == null)
    {
    throw new IllegalArgumentException("The parameter oldTrajectories is null.");
    }
    if (newTrajectories == null || newTrajectories.size() == 0)
    {
    return detect(oldTrajectories, oldDetectors, stepLimit);
    }

    CycleDetector[] cycleDetectors = new CycleDetector[oldDetectors.length + newTrajectories.size()];
    System.arraycopy(oldDetectors, 0, cycleDetectors, 0, oldDetectors.length);
    for (int i=oldDetectors.length; i<cycleDetectors.length; i++)
    {
    cycleDetectors[i] = factory.create();
    }

    ListMutableDataBlock<Trajectory> allTrajectories = new ListMutableDataBlock(oldTrajectories);
    allTrajectories.merge(newTrajectories);

    return this.detect(allTrajectories, (CD[])cycleDetectors, stepLimit);
    }
     */
}
