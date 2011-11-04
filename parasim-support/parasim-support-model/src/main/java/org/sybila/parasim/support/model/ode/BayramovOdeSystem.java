package org.sybila.parasim.support.model.ode;

import org.sybila.parasim.model.ode.ArrayOdeSystemEncoding;
import org.sybila.parasim.model.ode.DefaultOdeSystem;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class BayramovOdeSystem extends DefaultOdeSystem {

    public BayramovOdeSystem() {
        super(new ArrayOdeSystemEncoding(
            new int[] {0, 2, 6, 8},
            new float[] {(float) 0.0005, (float) -250, (float) 0.00001, (float) -0.1, (float) -250, (float) 300, (float) 250, (float) -300},
            new int[] {0, 0, 2, 2, 3, 5, 7, 9, 11},
            new int[] {0, 1, 1, 0, 1, 1, 2, 0, 1, 1, 2}
        ));
    }
    
}
