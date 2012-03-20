package org.sybila.parasim.computation.cycledetection.api;

//package org.sybila.parasim.computation.cycledetection;
//
//import org.sybila.parasim.computation.Module;
//import org.sybila.parasim.computation.ModuleComputationException;
//import org.sybila.parasim.model.trajectory.ArrayDataBlock;
//import org.sybila.parasim.model.trajectory.DataBlock;
//import org.sybila.parasim.model.trajectory.MixedOutput;
//import org.sybila.parasim.model.trajectory.Trajectory;
//import org.sybila.parasim.model.trajectory.CyclicTrajectory;
//import org.sybila.parasim.model.trajectory.LinkedCyclicTrajectory;
//import java.util.Arrays;
//
///**
// * Enables cycle detection on datablocks of trajectories given a specific
// * type of cycle detector and it's factory.
// * 
// * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
// */
//public class CycleDetectionModule<CD extends CycleDetector,
//                                  CDF extends CycleDetectorFactory<CD>, 
//                                  CT extends CyclicTrajectory>
////implements Module<DataBlock, CycleDetectDataBlock<Trajectory, CD>, DataBlock<CT>>
//{
//
//    private Detector<CD, CDF, CycleDetectDataBlock<Trajectory, CD>> detector;
//    private CDF factory;
//
//    public CycleDetectionModule(Detector<CD, CDF, CycleDetectDataBlock<Trajectory, CD>> detector, CDF factory)
//    {
//        if (detector == null) {
//            throw new IllegalArgumentException("The parameter detector is null.");
//        }        
//        if (factory == null) {
//            throw new IllegalArgumentException("The parameter factory is null.");
//        }
//        this.detector = detector;
//        this.factory = factory;
//    }
//
//    @Override
//    public DataBlock<CT> compute(DataBlock input) throws ModuleComputationException
//    {
//        return compute(input, null, 0).getOutput();
//    }
//
//    @Override
//    public MixedOutput<DataBlock<CT>, CycleDetectDataBlock<Trajectory, CD>>
//           compute(DataBlock input, CycleDetectDataBlock<Trajectory, CD> inter, int stepLimit)
//    {
//        CycleDetectDataBlock<Trajectory, CD> result1 = detector.detect(factory, input, stepLimit);
//        int counter = 0;
//        for (int i = 0; i<result1.size(); i++)
//        {
//            if (result1.getStatus(i) != CycleDetectionStatus.COMPUTING)
//            {
//                counter++;
//            }
//        }
//        CycleDetectDataBlock<Trajectory, CD> result2 = detector.detect(inter, stepLimit);
//        for (int i = 0; i<result2.size(); i++)
//        {
//            if (result2.getStatus(i) != CycleDetectionStatus.COMPUTING)
//            {
//                counter++;
//            }
//        }
//        CyclicTrajectory[] finishedTrajectories = new LinkedCyclicTrajectory[counter];
//        int otherCounter = result1.size() + result2.size() - counter;
//        Trajectory[] otherTrajectories = new Trajectory[otherCounter];
//        CD[] cycleDetectors = (CD[]) new CycleDetector[otherCounter];
//        CycleDetectionStatus[] statuses = new CycleDetectionStatus[otherCounter];
//        Arrays.fill(statuses, CycleDetectionStatus.COMPUTING);
//        counter = 0;
//        otherCounter = 0;
//        for (int i = 0; i<result1.size(); i++)
//        {
//            if (result1.getStatus(i) != CycleDetectionStatus.COMPUTING)
//            {
//                finishedTrajectories[counter++] = new LinkedCyclicTrajectory(result1.getTrajectory(i));
//            }
//            else
//            {
//                otherTrajectories[otherCounter] = result1.getTrajectory(i);
//                cycleDetectors[otherCounter++] = result1.getCycleDetector(i);
//            }
//        }
//        for (int i = 0; i<result1.size(); i++)
//        {
//            if (result2.getStatus(i) != CycleDetectionStatus.COMPUTING)
//            {
//                finishedTrajectories[counter++] = new LinkedCyclicTrajectory(result2.getTrajectory(i));
//            }
//            else
//            {
//                otherTrajectories[otherCounter] = result2.getTrajectory(i);
//                cycleDetectors[otherCounter++] = result2.getCycleDetector(i);
//            }
//        }
//        ArrayDataBlock finished = new ArrayDataBlock(finishedTrajectories);
//        ArrayDataBlock other = new ArrayDataBlock(otherTrajectories);
//        CycleDetectDataBlock<Trajectory, CD> interNew = new ArrayCycleDetectionDataBlock<Trajectory, CD>(other, cycleDetectors, statuses);
//
//        return new MixedOutput<DataBlock<CT>, CycleDetectDataBlock<Trajectory, CD>>(finished, interNew);
//    }    
//
//    /**
//     * Returns the CycleDetectionFactory used to create CycleDetectors.
//     *
//     * @return CycleDetectorFactory.
//     */
//    public CycleDetectorFactory getComparator()
//    {
//        return factory;
//    }
//
//    /**
//     * Sets the CycleDetectionFactory used to create CycleDetectors.
//     *
//     * @param factory The new CycleDetectorFactory.
//     */
//    public void setComparator(CDF factory) {
//        if (factory == null)
//        {
//            throw new IllegalArgumentException("The parameter comparator is null.");
//        }
//        this.factory = factory;
//    }
//
//}
