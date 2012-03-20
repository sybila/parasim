package org.sybila.parasim.computation.simulation.cpu;

import org.testng.annotations.Test;
import org.sybila.parasim.computation.simulation.AbstractAdaptiveStepSimulationTest;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.Simulator;
import org.testng.SkipException;

/**
 *
 * @author jpapouse
 */
public class TestOctaveSimulator extends AbstractAdaptiveStepSimulationTest {

    @Test
    public void testTimeStep() {
        if (!OctaveSimulator.isAvailable()) {
            throw new SkipException("The Octave is not available.");
        }
        super.testTimeStep(10);
    }
    
    @Test
    public void testMinimalNumberOfPoints() {
        if (!OctaveSimulator.isAvailable()) {
            throw new SkipException("The Octave is not available.");
        }
        super.testMinimalNumberOfPoints(10);
    }
    
    @Test
    public void testValidNumberOfTrajectories() {
        if (!OctaveSimulator.isAvailable()) {
            throw new SkipException("The Octave is not available.");
        }
        super.testValidNumberOfTrajectories(10);
    }

    @Override
    protected Simulator<AdaptiveStepConfiguration, SimulatedDataBlock> createSimulator(AdaptiveStepConfiguration configuaration) {
        return new OctaveSimulator();
    }
    
}
