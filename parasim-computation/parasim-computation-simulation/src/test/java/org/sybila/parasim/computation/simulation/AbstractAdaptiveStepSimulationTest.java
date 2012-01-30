package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractAdaptiveStepSimulationTest extends AbstractSimulatorTest<AdaptiveStepConfiguration, SimulatedDataBlock<Trajectory>> {

    protected void testAbsoluteStep(int size) {
        SimulatedDataBlock<Trajectory> result = getSimulator().simulate(getConfiguration(), createDataBlock(getConfiguration().getDimension(), size));
        for (int s = 0; s < size; s++) {
            Point previous = null;
            for (Point p : result.getTrajectory(s)) {
                if (previous == null) {
                    previous = p;
                    continue;
                }
                for(int dim=0; dim<p.getDimension(); dim++) {
                    assertTrue(Math.abs(previous.getValue(dim) - p.getValue(dim)) < getConfiguration().getSteps()[dim], "The absolute step condition in dimension <" + dim + "> doesn't hold. Found absolute step is <" + Math.abs(previous.getValue(dim) - p.getValue(dim)) + ">, expected <" + getConfiguration().getSteps()[dim] + ">");
                }
                previous = p;
            }
        }
    }

    @Override
    protected AdaptiveStepConfiguration createConfiguration() {
        final OdeSystem odeSystem = getOdeSystem(10);
        return new AdaptiveStepConfiguration() {

            private float[] maxAbsoluteError;
            private OrthogonalSpace space;
            private float[] steps;

            @Override
            public float[] getMaxAbsoluteError() {
                if (maxAbsoluteError == null) {
                    maxAbsoluteError = new float[getDimension()];
                    for (int dim = 0; dim < getDimension(); dim++) {
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
            public int getMaxNumberOfIterations() {
                return 100;
            }

            @Override
            public OdeSystem getOdeSystem() {
                return odeSystem;
            }

            public OrthogonalSpace getSpace() {
                if (space == null) {
                    float[] minBounds = new float[getDimension()];
                    for (int dim = 0; dim < getDimension(); dim++) {
                        minBounds[dim] = 0;
                    }
                    float[] maxBounds = new float[getDimension()];
                    for (int dim = 0; dim < getDimension(); dim++) {
                        maxBounds[dim] = (dim + 1) * 10000;
                    }
                    space = new OrthogonalSpace(new ArrayPoint(minBounds, 0), new ArrayPoint(maxBounds, (float) 100));
                }
                return space;
            }
            
            @Override
            public float[] getSteps() {
                if (steps == null) {
                    steps = new float[getDimension()];
                    for (int dim = 0; dim < getDimension(); dim++) {
                        steps[dim] = 1;
                    }
                }
                return steps;
            }

            @Override
            public float getTimeStep() {
                return (float) 0.01;
            }
        };
    }
}
