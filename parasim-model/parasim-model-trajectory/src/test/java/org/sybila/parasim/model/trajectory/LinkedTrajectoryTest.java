package org.sybila.parasim.model.trajectory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LinkedTrajectoryTest extends AbstractTrajectoryTest<LinkedTrajectory> {
    
    private LinkedTrajectory trajectory;
    private Point[] points;
    
    @BeforeMethod
    public void setUp() {
        int pointIndex = 0;
        points = new Point[(int)((1 + LENGTH) * LENGTH / 2)];
        for(int length=1; length<=LENGTH; length++) {
            float[] data = new float[length * DIMENSION];
            float[] times = new float[length];
            for(int p=0; p<length; p++) {
                for(int dim=0; dim<DIMENSION; dim++) {
                    data[p * DIMENSION + dim] = (float) (length + p * 0.01 + 0.0001 * dim);
                    times[p] = (float) (length + p * 0.01);
                }
                points[pointIndex] = new ArrayPoint(data, times[p], p * DIMENSION, DIMENSION);
                pointIndex++;
            }
            
            if (length == 1) {
                trajectory = new LinkedTrajectory(new ArrayTrajectory(data, times, DIMENSION));
            }
            else {
                trajectory.append(new ArrayTrajectory(data, times, DIMENSION));
            }
            
        }
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
