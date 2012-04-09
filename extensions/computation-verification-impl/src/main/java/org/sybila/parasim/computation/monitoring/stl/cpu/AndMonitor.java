package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.verification.stl.TimeInterval;
import java.util.List;
import java.util.ArrayList;
import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.ListIterator;

/**
 * Monitors the conjunction of two subformulas. The output is the minimum of
 * the two signals.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @param <T> Type of trajectory over which to monitor expression robustness.
 */
public class AndMonitor<T extends Trajectory>
        implements Evaluable<T, SimplePropertyRobustness> {

    private Evaluable<T, PropertyRobustness> sub1;
    private Evaluable<T, PropertyRobustness> sub2;

    public AndMonitor(Evaluable<T, PropertyRobustness> sub1, Evaluable<T, PropertyRobustness> sub2) {
        if (sub1 == null) {
            throw new IllegalArgumentException("Parameter sub1 is null.");
        }
        if (sub2 == null) {
            throw new IllegalArgumentException("Parameter sub2 is null.");
        }
        this.sub1 = sub1;
        this.sub2 = sub2;
    }

    /**
     * Evaluates the <b>and</b> function (min) of two given signals over the given interval.
     * Both signals are expected to start at the begining of the interval.
     *
     * @param signal1 First signal
     * @param signal2 Second signal
     * @param interval Time interval over which to evaluate
     * @return The <b>and</b> of the two signals (the minimum).
     */
    protected List<SimplePropertyRobustness> evaluate(List<PropertyRobustness> signal1,
            List<PropertyRobustness> signal2,
            TimeInterval interval) {
        if (signal1 == null) {
            throw new IllegalArgumentException("Parameter signal1 is null.");
        }
        if (signal2 == null) {
            throw new IllegalArgumentException("Parameter signal2 is null.");
        }
        if (interval == null) {
            throw new IllegalArgumentException("Parameter interval is null.");
        }
        if (signal1.isEmpty()) {
            throw new IllegalArgumentException("List signal1 is empty.");
        }
        if (signal2.isEmpty()) {
            throw new IllegalArgumentException("List signal2 is empty.");
        }

        ListIterator<PropertyRobustness> iterator1 = signal1.listIterator();
        ListIterator<PropertyRobustness> iterator2 = signal2.listIterator();

        /* Initialization */

        /* both lists are expected to be non-empty */
        PropertyRobustness pr1 = iterator1.next(); /* pr1 will hold last valid value of signal 1 */
        PropertyRobustness pr2 = iterator2.next(); /* pr2 will hold last valid value of signal 2 */

        if (pr1.getTime() != interval.getLowerBound()) {
            throw new IllegalArgumentException("First signal has bad start (" + pr1.getTime() + " != " + interval.getLowerBound() + ").");
        }
        if (pr2.getTime() != interval.getLowerBound()) {
            throw new IllegalArgumentException("Second signal has bad start (" + pr2.getTime() + " != " + interval.getLowerBound() + ").");
        }

        ArrayList<SimplePropertyRobustness> result = new ArrayList<SimplePropertyRobustness>();

        PropertyRobustness next1; /* these will be used to find out the next point of change of either signal */
        PropertyRobustness next2;

        int lastValueOrigin = 0; /* 1 - signal1, 2 - list 2, 0 - both */
        int valueOrigin;
        int newValuesPresent; /* 1 - signal1, 2 - list 2, 0 - both */

        lastValueOrigin = min(pr1, pr2);
        result.add(new SimplePropertyRobustness(lastValueOrigin == 2 ? pr2 : pr1));

        /* Loop through both signals */
        while (iterator1.hasNext() && iterator2.hasNext()) {
            next1 = iterator1.next();
            next2 = iterator2.next();
            float relInterTime = 0.0f; /* relative intersection time */
            float absInterTime = 0.0f; /* absolute intersection time */
            newValuesPresent = 0; /* both signals present new values */

            if ((pr1.value() < pr2.value() && pr1.getValueDerivative() > pr2.getValueDerivative())
                    || (pr1.value() > pr2.value() && pr1.getValueDerivative() < pr2.getValueDerivative())) {
                /* compute relative time of signal intersection */
                relInterTime = Math.abs(pr1.value() - pr2.value()) / Math.abs(pr1.getValueDerivative() - pr2.getValueDerivative());
                absInterTime = pr1.getTime() + relInterTime; /* pr1.getTime == pr2.getTime */
            }

            /* if there is an intersection and it occurs before the next event of any of the signals */
            if (relInterTime > 0.0f
                    && absInterTime < next1.getTime()
                    && absInterTime < next2.getTime()) {
                iterator1.previous();
                iterator2.previous();
                float interValue = pr1.value() + pr1.getValueDerivative() * relInterTime;
                next1 = new SimplePropertyRobustness(absInterTime, interValue, pr1.getValueDerivative());
                next2 = new SimplePropertyRobustness(absInterTime, interValue, pr2.getValueDerivative());
            } else if (next1.getTime() < next2.getTime()) /* no intersection, signal 1 has next event sooner */ {
                iterator2.previous();
                next2 = new SimplePropertyRobustness(
                        next1.getTime(),
                        pr2.value() + pr2.getValueDerivative() * (next1.getTime() - pr2.getTime()),
                        pr2.getValueDerivative());
                newValuesPresent = 1; /* signal 2 is rewinded, only signal 1 presents a new value */
            } else if (next1.getTime() > next2.getTime()) /* no intersection, signal 2 has next event sooner */ {
                iterator1.previous();
                next1 = new SimplePropertyRobustness(
                        next2.getTime(),
                        pr1.value() + pr1.getValueDerivative() * (next2.getTime() - pr1.getTime()),
                        pr1.getValueDerivative());
                newValuesPresent = 2; /* signal 1 is rewinded, only signal 2 presents a new value */
            }
            /* if next1.getTime() == next2.getTime(), values are ready for comparison */
            pr1 = next1;
            pr2 = next2;
            valueOrigin = min(pr1, pr2);
            //if (valueOrigin != lastValueOrigin || newValuesPresent == 0)
            /* If both signals presented new values or the minimum is a new value then add it */
            if (newValuesPresent == 0 || valueOrigin == newValuesPresent) {
                lastValueOrigin = valueOrigin;
                result.add(new SimplePropertyRobustness(lastValueOrigin == 2 ? pr2 : pr1));
            }
        }

        ListIterator<PropertyRobustness> iterator;
        PropertyRobustness fixed; /* final robustness of the shorter signal */
        PropertyRobustness pr;    /* last current robustness of the longer signal */
        PropertyRobustness next;  /* used to find out the next point of change of longer signal */
        if (iterator1.hasNext()) {
            iterator = iterator1;
            pr = pr1;
            fixed = pr2;
        } else {
            iterator = iterator2;
            pr = pr2;
            fixed = pr1;
        }

        /* process rest of longer signal */
        while (iterator.hasNext()) {
            next = iterator.next();
            float relInterTime = 0.0f; /* relative intersection time */
            float absInterTime = 0.0f; /* absolute intersection time */

            if ((fixed.value() < pr.value() && fixed.getValueDerivative() > pr.getValueDerivative())
                    || (fixed.value() > pr.value() && fixed.getValueDerivative() < pr.getValueDerivative())) {
                /* compute relative time of signal intersection */
                relInterTime = Math.abs(fixed.value() - pr.value()) / Math.abs(fixed.getValueDerivative() - pr.getValueDerivative());
                absInterTime = fixed.getTime() + relInterTime; /* fixed.getTime == pr.getTime */
            }

            /* if there is an intersection and it occurs before the next event of the longer signal */
            if (relInterTime > 0.0f
                    && absInterTime < next.getTime()) {
                iterator.previous();
                float interValue = fixed.value() + fixed.getValueDerivative() * relInterTime;
                next = new SimplePropertyRobustness(absInterTime, interValue, pr.getValueDerivative());
                fixed = new SimplePropertyRobustness(absInterTime, interValue, fixed.getValueDerivative());
            } else /* no intersection, compute fixed signal value in next.getTime() */ {
                fixed = new SimplePropertyRobustness(
                        next.getTime(),
                        fixed.value() + fixed.getValueDerivative() * (next.getTime() - fixed.getTime()),
                        fixed.getValueDerivative());
            }

            pr = next;
            valueOrigin = min(fixed, pr);
            /* If the signal source has changed or it comes from the longer signal add it */
            if (valueOrigin != lastValueOrigin || valueOrigin == 2) {
                lastValueOrigin = valueOrigin;
                result.add(new SimplePropertyRobustness(lastValueOrigin == 2 ? pr : fixed));
            }
        }

        /* find last possible intersection */
        if ((fixed.value() < pr.value() && fixed.getValueDerivative() > pr.getValueDerivative())
                || (fixed.value() > pr.value() && fixed.getValueDerivative() < pr.getValueDerivative())) {
            /* compute relative time of signal intersection */
            float relInterTime = Math.abs(fixed.value() - pr.value()) / Math.abs(fixed.getValueDerivative() - pr.getValueDerivative());
            float absInterTime = fixed.getTime() + relInterTime; /* fixed.getTime == pr.getTime */
            if (interval.smallerThenUpper(absInterTime)) {
                float interValue = fixed.value() + fixed.getValueDerivative() * relInterTime;
                pr = new SimplePropertyRobustness(absInterTime, interValue, pr.getValueDerivative());
                fixed = new SimplePropertyRobustness(absInterTime, interValue, fixed.getValueDerivative());
                lastValueOrigin = min(fixed, pr);
                result.add(new SimplePropertyRobustness(lastValueOrigin == 2 ? pr : fixed));
            }
        }

        return result;
    }

    @Override
    public List<SimplePropertyRobustness> evaluate(T trajectory, TimeInterval interval) {
        List<PropertyRobustness> signal1 = sub1.evaluate(trajectory, interval);
        List<PropertyRobustness> signal2 = sub2.evaluate(trajectory, interval);

        return evaluate(signal1, signal2, interval);
    }

    /**
     * Compares too values of property robustness and returns the minimum,
     * comparing first time (younger is first), value (smaller is first) and
     * value's derivative (smaller is first).
     *
     * @param pr1 First property robustness point.
     * @param pr2 Second property robustness point.
     *
     * @return Will return 1 if pr1 is "smaller" then pr2, 2 if vice versa and
     *         0 if pr1 == pr2.
     */
    private int min(PropertyRobustness pr1, PropertyRobustness pr2) {
        if (pr1.getTime() < pr2.getTime()) {
            return 1;
        } else if (pr1.getTime() > pr2.getTime()) {
            return 2;
        } else if (pr1.value() < pr2.value()) {
            return 1;
        } else if (pr1.value() > pr2.value()) {
            return 2;
        } else if (pr1.getValueDerivative() < pr2.getValueDerivative()) /* pr1.value() == pr2.value() */ {
            return 1;
        } else if (pr1.getValueDerivative() > pr2.getValueDerivative()) {
            return 2;
        } else /* pr1.value() == pr2.value() && pr1.getValueDerivative() == pr2.getValueDerivative() */ {
            return 0;
        }
    }

    @Override
    public String toString() {
        return sub1.toString() + " && " + sub2.toString();
    }
}
