package org.sybila.parasim.model.trajectory;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestArrayTrajectory extends AbstractTrajectoryTest<ArrayTrajectory> {
    
    private Point[] points;
    private Trajectory trajectory;
    
    @BeforeTest
    public void setUp() {
        points = new Point[LENGTH];
        float[] data = new float[LENGTH * DIMENSION];
        float[] times = new float[LENGTH];
        for(int p=0; p<LENGTH; p++) {
            for(int dim=0; dim<DIMENSION; dim++) {
                data[p * DIMENSION + dim] = (float) (p + 0.01 * dim);
                times[p] = p;
            }
            points[p] = new ArrayPoint(data, p, p * DIMENSION, DIMENSION);
        }
        trajectory = new ArrayTrajectory(data, times, DIMENSION);
    }
    
    @Test
    public void testPointSequenceWithIterator() {
        super.testPointSequenceWithIterator(trajectory, points);
    }

    @Test
    public void testPointSequenceWithGetMethod() {
        super.testPointSequenceWithGetMethod(trajectory, points);
    }    
    
}
