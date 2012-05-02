package org.sybila.parasim.model.verification;

/**
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class SimpleRobustness implements Robustness {

    private final float value;

    public SimpleRobustness(float value) {
        this.value = value;
    }

    public float getValue() {
        return this.value;
    }

}
