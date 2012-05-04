package org.sybila.parasim.model.trajectory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SimpleDistance implements Distance {

    private float value;

    public SimpleDistance(float value) {
        this.value = value;
    }

    @Override
    public float value() {
        return value;
    }
}
