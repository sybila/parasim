package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;

/**
 *
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public class Transition
{
    private int from, to;  /* origin and target state */
    private Evaluable guard; /* condition guarding the transition */

    public Transition(int from, int to, Evaluable guard)
    {
        this.from = from;
        this.to = to;
        this.guard = guard;
    }


    boolean enabled(Point p)
    {
        if (guard == null)
        {
            /* Some transitions have empty guards which are always true */
            return true;
        }
        else
        {
            return guard.valid(p);
        }
    }

    /**
     * @return the origin state
     */
    public int getFrom() {
        return from;
    }

    /**
     * @return the target state
     */
    public int getTo() {
        return to;
    }

    /**
     * @return the guard
     */
    public Evaluable getGuard() {
        return guard;
    }

}


