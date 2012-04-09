package org.sybila.parasim.computation.monitoring.stl.cpu;

import java.util.List;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Iterator;

import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;

/**
 * Caches results of monitoring procedures for future retrieval.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class ValueCache<PR extends PropertyRobustness> {
    /* contains already inserted intervals sorted by the time of their first point */

    private TreeSet<List<PR>> intervals;

    public ValueCache() {
        intervals = new TreeSet(new ListComparator());
    }

    /**
     * Inserts the values into the cache.
     * @param values
     */
    public void insert(List<PR> values) {
        Iterator it = intervals.iterator();

        while (it.hasNext()) {
            //FIXME
        }
    }

    /**
     * Retrieves the largest continuous interval of values existing in the cache
     * that is a subinterval of [a,b].
     *
     * @param a Begining of interval as absolute time.
     * @param b End of interval as absolute time.
     * @return List of values.
     */
    public List<PR> getIntersetion(float a, float b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private class ListComparator implements Comparator<List<PR>> {

        /**
         * Both lists must be non-null.
         * Note: this comparator imposes orderings that are inconsistent with equals.
         */
        public int compare(List<PR> list1, List<PR> list2) {
            if (list1 == null) {
                throw new NullPointerException("Parameter list1 is null.");
            }
            if (list1.isEmpty()) {
                throw new NullPointerException("Parameter list1 is empty.");
            }
            if (list2 == null) {
                throw new NullPointerException("Parameter list2 is null.");
            }
            if (list1.isEmpty()) {
                throw new NullPointerException("Parameter list1 is empty.");
            }
            return (int) (list1.get(0).getTime() - list2.get(0).getTime());
        }
    }
}
