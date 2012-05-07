package org.sybila.parasim.visualisation.plot.impl.layer;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestSingleLayerFourNeighbourhoodComparator extends NeighbourhoodComparatorTest<SingleLayerFourNeighbourhood> {

    @BeforeMethod
    public void init() {
        cmp = SingleLayerFourNeighbourhood.getComparator();
    }

    @Test
    public void testCompare() {
        SingleLayerFourNeighbourhood nNESW = new SingleLayerFourNeighbourhood(true, true, true, true);
        SingleLayerFourNeighbourhood nNSW = new SingleLayerFourNeighbourhood(true, false, true, true);
        SingleLayerFourNeighbourhood nNEW = new SingleLayerFourNeighbourhood(true, true, false, true);
        SingleLayerFourNeighbourhood nWE = new SingleLayerFourNeighbourhood(false, true, false, true);
        SingleLayerFourNeighbourhood nNS = new SingleLayerFourNeighbourhood(true, false, true, false);
        SingleLayerFourNeighbourhood nNW = new SingleLayerFourNeighbourhood(true, false, false, true);
        SingleLayerFourNeighbourhood nN = new SingleLayerFourNeighbourhood(true, false, false, false);
        SingleLayerFourNeighbourhood nW = new SingleLayerFourNeighbourhood(false, false, false, true);
        SingleLayerFourNeighbourhood n = new SingleLayerFourNeighbourhood(false, false, false, false);

        assertPriority(nNESW, nNSW);
        assertPriority(nNESW, nNEW);
        assertPriority(nNESW, nNW);
        assertPriority(nNESW, nWE);
        assertPriority(nNESW, nNS);
        assertPriority(nNESW, nN);
        assertPriority(nNESW, nW);
        assertPriority(nNESW, n);

        assertEquality(nNSW, nNEW);
        assertPriority(nNSW, nNW);
        assertPriority(nNSW, nWE);
        assertPriority(nNSW, nNS);
        assertPriority(nNSW, nN);
        assertPriority(nNSW, nW);
        assertPriority(nNSW, n);

        assertPriority(nNEW, nNW);
        assertPriority(nNEW, nWE);
        assertPriority(nNEW, nNS);
        assertPriority(nNEW, nN);
        assertPriority(nNEW, nW);
        assertPriority(nNEW, n);

        assertEquality(nWE, nNS);
        assertPriority(nWE, nNW);
        assertPriority(nWE, nN);
        assertPriority(nWE, nW);
        assertPriority(nWE, n);

        assertPriority(nNS, nNW);
        assertPriority(nNS, nN);
        assertPriority(nNS, nW);
        assertPriority(nNS, n);

        assertPriority(nNW, nN);
        assertPriority(nNW, nW);
        assertPriority(nNW, n);

        assertEquality(nN, nW);
        assertPriority(nN, n);
        assertPriority(nW, n);
    }

}
