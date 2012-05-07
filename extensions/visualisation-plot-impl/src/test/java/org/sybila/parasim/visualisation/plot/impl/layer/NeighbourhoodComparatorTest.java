package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.Comparator;
import org.testng.Assert;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class NeighbourhoodComparatorTest<T> {
    protected Comparator<T> cmp = null;

    private void checkComparator() {
        if (cmp == null) {
            Assert.fail("Comparator not initialized.");
        }
    }

    protected void assertPriority(T target, T over) {
        checkComparator();
        Assert.assertEquals(sgn(cmp.compare(target, over)), -1, "Neighbourhood " + target + " should have priority over " + over + ".");
    }

    protected void assertEquality(T t1, T t2) {
        checkComparator();
        Assert.assertEquals(sgn(cmp.compare(t1, t2)), 0, "Neighbourhoods " + t1 + " and " + t2 + " should have roughly the same priority.");
    }

    protected static int sgn(int x) {
        if (x > 0) {
            return 1;
        } else if (x < 0) {
            return -1;
        } else {
            assert x == 0;
            return 0;
        }
    }
}
