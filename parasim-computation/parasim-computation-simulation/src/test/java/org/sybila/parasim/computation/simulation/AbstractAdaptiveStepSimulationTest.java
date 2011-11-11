package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractAdaptiveStepSimulationTest extends AbstractSimulatorTest<AdaptiveStepConfiguration, SimulatedDataBlock<Trajectory>>{
    
    @Override
    protected AdaptiveStepConfiguration createConfiguration() {
        final OdeSystem odeSystem = getOdeSystem(10);
        return new AdaptiveStepConfiguration() {

            private float[] maxAbsoluteError;
            private float[] maxBounds;
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
            public float getMaxRelativeError() {
               return (float) 0.1;
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
                return (float) 0.01;
            }
        };
    }    
    
}
