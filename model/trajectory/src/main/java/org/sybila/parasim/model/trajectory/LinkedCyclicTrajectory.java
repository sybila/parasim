package org.sybila.parasim.model.trajectory;

/**
 * Combines the implementation of a LinkedTrajectory and a SimpleCycle to form
 * a Trajectory with a TrajectoryCycle.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class LinkedCyclicTrajectory extends LinkedTrajectory implements CyclicTrajectory
{
    private SimpleCycle cycle;

    public LinkedCyclicTrajectory(Trajectory trajectory)
    {
        super(trajectory);
        cycle = new SimpleCycle();
    }

    public LinkedCyclicTrajectory(Trajectory trajectory, int cycleStartPosition, int cycleEndPosition)
    {
        super(trajectory);
        cycle = new SimpleCycle(trajectory, cycleStartPosition, cycleEndPosition);
    }

    public boolean hasCycle()
    {
        return cycle.hasCycle();
    }

    public int getCycleStartPosition()
    {
        return cycle.getCycleStartPosition();
    }

    public int getCycleEndPosition()
    {
        return cycle.getCycleEndPosition();
    }

    public CyclicTrajectoryIterator cyclicIterator()
    {
        return cycle.cyclicIterator();
    }

    public CyclicTrajectoryIterator cyclicIterator(int index)
    {
        return cycle.cyclicIterator(index);
    }

}
