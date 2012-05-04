package org.sybila.parasim.model.verification;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class SimpleRobustness implements Robustness {

    private final float time;
    private final float value;
    private Robustness inverted;

    public SimpleRobustness(float value) {
        this(value, 0, null);
    }

    public SimpleRobustness(float value, float time) {
        this(value, time, null);
    }

    private SimpleRobustness(float value, float time, Robustness inverted) {
        this.value = value;
        this.inverted = inverted;
        this.time = time;
    }

    public float getTime() {
        return time;
    }

    public float getValue() {
        return this.value;
    }

    public Robustness invert() {
        if (value == 0) {
            return this;
        }
        if (inverted == null) {
            inverted = new SimpleRobustness(-value, time, this);
        }
        return inverted;
    }

}
