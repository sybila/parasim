package org.sybila.parasim.support.computation.simulation;

import org.sybila.parasim.computation.simulation.ImutableAdaptiveStepConfiguraton;

/*
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class BayramovAdaptiveStepConfiguration extends ImutableAdaptiveStepConfiguraton {
    
    public BayramovAdaptiveStepConfiguration() {
        super(new BayramovConfiguration(), new float[] {0, 0, 0}, 0);
    }
    
}
