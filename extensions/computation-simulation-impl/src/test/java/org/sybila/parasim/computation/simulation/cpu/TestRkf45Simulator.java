package org.sybila.parasim.computation.simulation.cpu;

import org.sybila.parasim.computation.simulation.cpu.Rkf45Simulator;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.Simulator;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.testng.annotations.Test;
import org.sybila.parasim.computation.simulation.AbstractAdaptiveStepSimulationTest;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestRkf45Simulator extends AbstractAdaptiveStepSimulationTest {

    @Test
    public void testAbsoluteStep() {
        super.testAbsoluteStep(10);
    }
    
    @Test
    public void testTimeStep() {
        super.testTimeStep(10);
    }

    @Test
    public void testMinimalNumberOfPoints() {
        super.testMinimalNumberOfPoints(10);
    }    
    
    @Test
    public void testValidNumberOfTrajectories() {
        super.testValidNumberOfTrajectories(10);
    }

    @Override
    protected Simulator<AdaptiveStepConfiguration, SimulatedDataBlock<Trajectory>> createSimulator(AdaptiveStepConfiguration configuaration) {
        return new Rkf45Simulator();
    }
    
}
