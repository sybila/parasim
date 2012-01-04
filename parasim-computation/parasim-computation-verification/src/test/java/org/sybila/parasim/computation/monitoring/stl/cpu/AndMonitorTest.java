package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.testng.annotations.Test;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;

/**
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class AndMonitorTest extends AbstractEvaluableTest<ArrayTrajectory, SimplePropertyRobustness>
{
    private AndMonitor<ArrayTrajectory> monitor;

    AndMonitorTest()
    {
     //   monitor = new AndMonitor()
    }

    
    public void testEvaluate()
    {
        
    }

    @Override
    public Evaluable<ArrayTrajectory, SimplePropertyRobustness> getMonitor()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ArrayTrajectory getTrajectory(int length, int dim, float time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SimplePropertyRobustness createRobustness(float time, float value, float derivative) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
