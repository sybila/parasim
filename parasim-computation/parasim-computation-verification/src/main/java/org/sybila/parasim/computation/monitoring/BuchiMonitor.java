package org.sybila.parasim.computation.monitoring;

import org.sybila.parasim.model.verification.buchi.Buchi;
import org.sybila.parasim.model.verification.buchi.BuchiStatus;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.Point;
import java.util.Set;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * Enables the monitoring of an LTL property represented by a Buchi automaton
 * over a single simulated trajectory.
 *
 * The amount of work carried out by the computation can be parametrized.
 * Therefore the computation can be stopped when the work limit is reached
 * or the end of the simulated trajectory is reached but more points are
 * expected to be simulated in the future.
 *
 * If the property is proved to be true or false over the existing portion
 * of a trajectory the computation is stopped.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class BuchiMonitor {

    private Buchi automaton;
    private Trajectory trajectory;
    /** Current set of reached states of the automaton */
    private Set<Integer> states;
    /** Position of computation on trajectory, successors of states at this
        point will be computed in next compute step. */
    private int position;
    /** Status of the computation */
    private BuchiStatus status;

    public BuchiMonitor(Buchi automaton, Trajectory trajectory)
    {
        this.automaton = automaton;
        this.trajectory = trajectory;
        states = new TreeSet(automaton.getInitialStates());
        position = 0;
    }

    public BuchiStatus getStatus()
    {
        return status;
    }

    /**
     * Performs the monitoring over a portion of the trajectory processing
     * at most stepLimit points. Returns the actual number of steps used.
     *
     * @param stepLimit maximum number of points to monitor in this computation
     *        batch
     * @return number of points processed on trajectory
     */
    public int compute(int stepLimit)
    {
        Iterator<Point> it = trajectory.iterator(position);
        int stepsUsed = 0;
        while (stepsUsed < stepLimit && it.hasNext())
        {
            Point p = it.next();//FIXME cycle
            states = automaton.getSuccessors(states, p);
        }
        return stepsUsed;
    }

}
