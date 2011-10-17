/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.support.computation.simulation;

import org.sybila.parasim.computation.simulation.ImmutableConfiguration;
import org.sybila.parasim.support.model.ode.LotkaVolteraOdeSystem;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LotkaVolteraConfiguration extends ImmutableConfiguration {
    
    public LotkaVolteraConfiguration() {
        super(new LotkaVolteraOdeSystem(), new float[] {100, 100}, new float[] {0, 0}, 100);
    }
    
}
