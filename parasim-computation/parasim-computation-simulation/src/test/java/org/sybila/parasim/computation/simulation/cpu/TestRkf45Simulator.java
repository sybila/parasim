package org.sybila.parasim.computation.simulation.cpu;

import org.sybila.parasim.computation.simulation.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.Simulator;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.testng.annotations.Test;
import org.sybila.parasim.computation.simulation.AbstractAdaptiveStepSimulationTest;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestRkf45Simulator extends AbstractAdaptiveStepSimulationTest {

    @Test
    public void testValidNumberOfTrajectories() {
        super.testValidNumberOfTrajectories(10);
    }

    @Override
    protected Simulator<AdaptiveStepConfiguration, SimulatedDataBlock<Trajectory>> createSimulator(AdaptiveStepConfiguration configuaration) {
        return new Rkf45Simulator();
    }
    
}
