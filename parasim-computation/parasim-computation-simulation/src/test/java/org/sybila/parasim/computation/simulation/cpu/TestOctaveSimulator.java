package org.sybila.parasim.computation.simulation.cpu;

import org.junit.Test;
import org.sybila.parasim.computation.simulation.AbstractAdaptiveStepSimulationTest;
import org.sybila.parasim.computation.simulation.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.Simulator;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 *
 * @author jpapouse
 */
public class TestOctaveSimulator extends AbstractAdaptiveStepSimulationTest {

    @Test
    public void testValidNumberOfTrajectories() {
        if (OctaveSimulator.isAvailable()) {
            super.testValidNumberOfTrajectories(10);
        }
    }

    @Override
    protected Simulator<AdaptiveStepConfiguration, SimulatedDataBlock<Trajectory>> createSimulator(AdaptiveStepConfiguration configuaration) {
        return new OctaveSimulator();
    }
    
}
