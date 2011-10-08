package org.sybila.parasim.computation.simulation.cpu;

import org.junit.Test;
import org.sybila.parasim.computation.simulation.AbstractSimulatorTest;
import org.sybila.parasim.computation.simulation.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.DataBlock;
import org.sybila.parasim.computation.simulation.Simulator;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestRkf45Simulator extends AbstractSimulatorTest<AdaptiveStepConfiguration, DataBlock<Trajectory>> {

    @Test
    public void testValidNumberOfTrajectories() {
        super.testValidNumberOfTrajectories(10);
    }
    
    @Override
    protected AdaptiveStepConfiguration createConfiguration() {
        final OdeSystem odeSystem = getOdeSystem(10);
        return new AdaptiveStepConfiguration() {

            private float[] maxAbsoluteError;
            private float[] maxBounds;
            private float[] maxRelativeError;
            private float[] minBounds;
            private float[] steps;
            
            @Override
            public float[] getMaxAbsoluteError() {
                if (maxAbsoluteError == null) {
                    maxAbsoluteError = new float[getDimension()];
                    for(int dim = 0; dim < getDimension(); dim++) {
                        maxAbsoluteError[dim] = 1;
                    }
                }
                return maxAbsoluteError;
            }

            @Override
            public float[] getMaxRelativeError() {
                if (maxRelativeError == null) {
                    maxRelativeError = new float[getDimension()];
                    for(int dim = 0; dim < getDimension(); dim++) {
                        maxRelativeError[dim] = (float)0.1;
                    }
                }
                return maxRelativeError;
            }

            @Override
            public int getDimension() {
                return 10;
            }

            @Override
            public float[] getMaxBounds() {
                if (maxBounds == null) {
                    maxBounds = new float[getDimension()];
                    for (int dim = 0; dim < getDimension(); dim++) {
                        maxBounds[dim] = (dim + 1) * 10000;
                    }
                }
                return maxBounds;
            }

            @Override
            public int getMaxNumberOfIterations() {
                return 100;
            }

            @Override
            public float[] getMinBounds() {
                if (minBounds == null) {
                    minBounds = new float[getDimension()];
                    for (int dim = 0; dim < getDimension(); dim++) {
                        minBounds[dim] = 0;
                    }
                }
                return minBounds;
            }

            @Override
            public OdeSystem getOdeSystem() {
                return odeSystem;
            }

            @Override
            public float[] getSteps() {
                if (steps == null) {
                    steps = new float[getDimension()];
                    for(int dim = 0; dim < getDimension(); dim++) {
                        steps[dim] = 1;
                    }
                }
                return steps;
            }

            @Override
            public float getTargetTime() {
                return (float) 100;
            }

            @Override
            public float getTimeStep() {
                return (float) 0.0001;
            }
        };
    }

    @Override
    protected Simulator<AdaptiveStepConfiguration, DataBlock<Trajectory>> createSimulator(AdaptiveStepConfiguration configuaration) {
        return new Rkf45Simulator();
    }
    
}
