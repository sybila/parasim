package org.sybila.parasim.computation.density.spawn.cpu;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestPosition {

    private static final int DEPTH_LIMIT = 10;

    @Test
    public void testSimpleFloatAddition() {
        float numberX = 0;
        float numberY = 1;
        float toAdd = 1;
        float toSubtract = 1;
        for (int i = 0; i < DEPTH_LIMIT; i++) {
            toAdd /= 2;
            toSubtract /= 2;
            numberX += toAdd;
            numberY -= toSubtract;
        }
        assertEquals((float) 1.0 - numberX, numberY);
    }

    @Test
    public void testComplicatedSmallFloatAddition() {
        float[] numbers = new float[]{
            (float) 0.984327598432709847,
            (float) 45.01092843649643093,
            (float) 37.094387439843722303,
            (float) 0.000000000000432,
        };
        for (float number : numbers) {
            testFloatAddition(number);
        }
    }

    @Test
    public void testComplicatedBigFloatAddition() {
        float[] numbers = new float[] {
            (float) 93439.0322,
            (float) 33323833.063298,
        };
        for (float number : numbers) {
            testFloatAddition(number);
        }        
    }
    
    private void testFloatAddition(float number) {
        float numberX = number;
        float numberY = number;
        float toAdd = 1;
        float toSubtract = 1;
        float sum = 0;
        for (int i = 0; i < DEPTH_LIMIT; i++) {
            toAdd /= 2;
            toSubtract /= 2;
            assertNotEquals(toAdd * number, 0);
            sum += toAdd * number;
            sum += toSubtract * number;
            numberX += toAdd * number;
            numberY -= toSubtract * number;
        }
        assertEquals(numberX - numberY, sum, "Tested number <" + number + ">.");
        assertNotEquals(numberY, 0);
    }
}
