package org.sybila.parasim.computation.monitoring.stl.cpu;

import java.util.List;
import java.util.ArrayList;
import org.sybila.parasim.computation.monitoring.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.ListIterator;

/**
 * Monitors the conjunction of two subformulas. The output is the minimum of
 * the two signals.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class AndMonitor<T extends Trajectory>
       implements Evaluable<T, SimplePropertyRobustness>
{
    private Evaluable<T, PropertyRobustness> sub1;
    private Evaluable<T, PropertyRobustness> sub2;

    public AndMonitor(Evaluable<T, PropertyRobustness> sub1, Evaluable<T, PropertyRobustness> sub2)
    {
        if (sub1 == null)
        {
            throw new NullPointerException("Parameter sub1 is null.");
        }
        if (sub2 == null)
        {
            throw new NullPointerException("Parameter sub2 is null.");
        }
        this.sub1 = sub1;
        this.sub2 = sub2;
    }

    public List<SimplePropertyRobustness> evaluate(T trajectory, float a, float b)
    {
        List<PropertyRobustness> list1 = sub1.evaluate(trajectory, a, b);
        List<PropertyRobustness> list2 = sub2.evaluate(trajectory, a, b);
        ArrayList<SimplePropertyRobustness> result = new ArrayList<SimplePropertyRobustness>();
        int lastValueOrigin = 0; // 1 - list1, 2 - list 2, 0 - both

        ListIterator<PropertyRobustness> iterator1 = list1.listIterator();
        ListIterator<PropertyRobustness> iterator2 = list2.listIterator();

        if (!iterator1.hasNext())
        {
            throw new RuntimeException("First signal is empty.");
        }
        if (!iterator2.hasNext())
        {
            throw new RuntimeException("Second signal is empty.");
        }

        /* Initialization */
        
        /* both lists are expected to be non-empty */
        PropertyRobustness pr1 = iterator1.next(); /* pr1 will hold last valid value of signal 1 */
        PropertyRobustness pr2 = iterator2.next(); /* pr2 will hold last valid value of signal 2 */
        PropertyRobustness next1; /* these will be used to find out the next point of change of either signal */
        PropertyRobustness next2;
        if (pr1.getTime() != a)
        {
            throw new RuntimeException("First signal has bad start (" + pr1.getTime() + " != " + a + ").");
        }
        if (pr2.getTime() != a)
        {
            throw new RuntimeException("Second signal has bad start (" + pr2.getTime() + " != " + a + ").");
        }
        lastValueOrigin = min(pr1, pr2);
        result.add(new SimplePropertyRobustness(lastValueOrigin == 2?pr2:pr1));

        /* Loop through both signals */
        while (iterator1.hasNext() && iterator2.hasNext()) //FIXME
        {                        
            if (lastValueOrigin == 0)
            {
                pr1 = iterator1.next();
                pr2 = iterator2.next();
                lastValueOrigin = min(pr1, pr2);
                result.add(new SimplePropertyRobustness(lastValueOrigin == 2?pr2:pr1));
            }
            else if (lastValueOrigin == 1)
            {
                if (pr1.getValueDerivative() == pr2.getValueDerivative())
                /* No intersection is possible */
                {
                    next1 = iterator1.next();
                    next2 = iterator2.next();
                    if (next1.getTime() == next2.getTime())
                    {
                        pr1 = next1;
                        pr2 = next2;
                        lastValueOrigin = min(pr1, pr2);
                        result.add(new SimplePropertyRobustness(lastValueOrigin == 2?pr2:pr1));
                    }
                    else if(next1.getTime() < next2.getTime())
                    {
                        PropertyRobustness pr2Now =
                                new SimplePropertyRobustness(
                                        next1.getTime(),
                                        pr2.value() + pr2.getValueDerivative() * (next1.getTime() - pr2.getTime()),
                                        pr2.getValueDerivative());
                        lastValueOrigin = min(next1, pr2Now);
                        result.add(new SimplePropertyRobustness(lastValueOrigin == 2?pr2Now:next1));
                        pr1 = next1;
                        iterator2.previous();
                    }
                    else if (next1.getTime() > next2.getTime())
                    {
                        //FIXME
                        pr2 = next2;
                        iterator1.previous();
                    }
                }
                else if (pr1.getValueDerivative() < pr2.getValueDerivative())
                {
                    next1 = iterator1.next();
                    next2 = iterator2.next();
                    //FIXME
                }
                else /* (pr1.getValueDerivative() > pr2.getValueDerivative()) */
                {

                }
                
                //pr2 = iterator2.next();
                //FIXME
            }
        }

        //FIXME process rest of signal
        while (iterator1.hasNext())
        {

        }
        while (iterator2.hasNext())
        {

        }
        
    }

    /* A mutable wrapper for a int for passing values out of methods that return
       another value. */
//    private class MutableInt
//    {
//        private int value;
//
//        MutableInt(int value)
//        {
//            this.value = value;
//        }
//
//        public int getValue()
//        {
//            return value;
//        }
//
//        public void setValue(int value)
//        {
//            this.value = value;
//        }
//    }

    /**
     * Compares too values of property robustness and returns the minimum,
     * comparing first time (younger is first), value (smaller is first) and
     * value's derivative (smaller is first).
     *
     * @param pr1 First property robustness point.
     * @param pr2 Second property robustness point.
     * @param origin Mutable int to mark origin of result.
     *        Will contain 1 if returned value is pr1, 2 if returned value is pr2
     *        and 0 if pr1 == pr2.
     * @return The smaller of the two points.
     */
//    private PropertyRobustness min(PropertyRobustness pr1, PropertyRobustness pr2, MutableInt origin)
//    {
//        if (pr1.getTime() < pr2.getTime())
//        {
//            origin.setValue(1);
//            return pr1;
//        }
//        else if (pr1.getTime() > pr2.getTime())
//        {
//            origin.setValue(2);
//            return pr2;
//        }
//        else if (pr1.value() < pr2.value())
//        {
//            origin.setValue(1);
//            return pr1;
//        }
//        else if (pr1.value() > pr2.value())
//        {
//            origin.setValue(2);
//            return pr2;
//        }
//        else if (pr1.getValueDerivative() < pr2.getValueDerivative()) /* pr1.value() == pr2.value() */
//        {
//            origin.setValue(1);
//            return pr1;
//        }
//        else if (pr1.getValueDerivative() > pr2.getValueDerivative())
//        {
//            origin.setValue(2);
//            return pr2;
//        }
//        else /* pr1.value() == pr2.value() && pr1.getValueDerivative() == pr2.getValueDerivative() */
//        {
//            origin.setValue(0);
//            return pr1;
//        }
//    }

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
    private int min(PropertyRobustness pr1, PropertyRobustness pr2)
    {
        if (pr1.getTime() < pr2.getTime())
        {
            return 1;
        }
        else if (pr1.getTime() > pr2.getTime())
        {
            return 2;
        }
        else if (pr1.value() < pr2.value())
        {
            return 1;
        }
        else if (pr1.value() > pr2.value())
        {
            return 2;
        }
        else if (pr1.getValueDerivative() < pr2.getValueDerivative()) /* pr1.value() == pr2.value() */
        {
            return 1;
        }
        else if (pr1.getValueDerivative() > pr2.getValueDerivative())
        {
            return 2;
        }
        else /* pr1.value() == pr2.value() && pr1.getValueDerivative() == pr2.getValueDerivative() */
        {
            return 0;
        }
    }

}
