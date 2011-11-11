package org.sybila.parasim.support.model.ode;

import org.sybila.parasim.model.ode.ArrayOdeSystemEncoding;
import org.sybila.parasim.model.ode.DefaultOdeSystem;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LotkaVolteraOdeSystem extends DefaultOdeSystem {

    public LotkaVolteraOdeSystem() {
        super(new ArrayOdeSystemEncoding(
            new int[] {0, 2, 4},
            new float[] {(float) 10.1, (float) -1, (float) 1, (float) -5.4},
            new int[] {0, 1, 3, 5, 6},
            new int[] {0, 0, 1, 0, 1, 1}
        ));
    }    
}
