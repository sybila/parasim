package org.sybila.parasim.computation.cycledetection;

import java.util.Iterator;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Represents a cycle detection data block of trajectories being cycle detected.
 * All cycle detectors and computation statuses are held in arrays.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class ArrayCycleDetectionDataBlock<T extends Trajectory> implements CycleDetectDataBlock<T>
{
    private DataBlock<T> dataBlock;
    private CycleDetector[] cycleDetectors;
    private CycleDetectionStatus[] statuses;

    public ArrayCycleDetectionDataBlock(DataBlock<T> dataBlock,
            CycleDetector[] cycleDetectors, CycleDetectionStatus[] statuses)
    {
        if (dataBlock == null)
        {
            throw new IllegalArgumentException("The parameter dataBlock is null.");
        }
        if (cycleDetectors == null)
        {
            throw new IllegalArgumentException("The parameter cycleDetectors is null.");
        }        
        if (statuses == null)
        {
            throw new IllegalArgumentException("The parameter statuses is null.");
        }
        if (dataBlock.size() != cycleDetectors.length)
        {
            throw new IllegalArgumentException("The number of trajectories does not match number of cycle detectors.");
        }        
        if (dataBlock.size() != statuses.length)
        {
            throw new IllegalArgumentException("The number of trajectories in data block doesn't match the number of statuses.");
        }
        this.dataBlock = dataBlock;
        this.cycleDetectors = cycleDetectors;        
        this.statuses = statuses;
    }

    @Override
    public CycleDetector getCycleDetector(int index)
    {
        if (index < 0 || index > cycleDetectors.length)
        {
            throw new IllegalArgumentException("Index must be in range [0, "+cycleDetectors.length+").");
        }
        return cycleDetectors[index];
    }

    @Override
    public CycleDetectionStatus getStatus(int index) 
    {
        if (index < 0 || index > statuses.length)
        {
            throw new IllegalArgumentException("Index must be in range [0, "+statuses.length+")");
        }
        return statuses[index];
    }

    @Override
    public T getTrajectory(int index) 
    {
        if (index < 0 || index > dataBlock.size())
        {
            throw new IllegalArgumentException("Index must be in range [0, "+dataBlock.size()+")");
        }
        return dataBlock.getTrajectory(index);
    }

    @Override
    public Iterator<T> iterator()
    {
        return dataBlock.iterator();
    }

    @Override
    public int size()
    {
        return dataBlock.size();
    }

}
