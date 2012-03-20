/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.computation.density.api;

import org.sybila.parasim.model.trajectory.Distance;

/**
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface LimitedDistance extends Distance {
    
    /**
     * Checks whether the distance between two given object is valid.
     */
    boolean isValid();    
    
}
