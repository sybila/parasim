package org.sybila.parasim.support.computation.simulation;

import org.sybila.parasim.computation.simulation.ImutableAdaptiveStepConfiguraton;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LotkaVolteraAdaptiveStepConfiguration extends ImutableAdaptiveStepConfiguraton {
    
    public LotkaVolteraAdaptiveStepConfiguration() {
        super(new LotkaVolteraConfiguration(), new float[] {0, 0}, new float[] {0, 0});
    }
    
}
