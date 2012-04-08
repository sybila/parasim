package org.sybila.parasim.computation.simulation;

import org.apache.commons.lang3.Validate;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtensionConfiguration {
    
    private Float[] maxAbsoluteError;
    private float maxRelativeError;

    public Float[] getMaxAbsoluteError() {
        return maxAbsoluteError;
    }

    public float getMaxRelativeError() {
        return maxRelativeError;
    }

    public void validate() {
        Validate.notNull(maxAbsoluteError);
        Validate.notNull(maxRelativeError);
    }
    
}
