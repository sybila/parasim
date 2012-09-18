package org.sybila.parasim.visualisation.projection.api.scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class LinearScale implements Scale {

    private float min, factor;

    private LinearScale(float min, float factor) {
        this.min = min;
        this.factor = factor;
    }

    @Override
    public ScaleType getType() {
        return ScaleType.LINEAR;
    }

    @Override
    public float getModelCoordinate(int screenCoordinate) {
        return screenCoordinate * factor + min;
    }

    @Override
    public int getViewCoordinate(float modelCoordinate) {
        return Float.valueOf((modelCoordinate - min) / factor).intValue();
    }

    static Scale getFromSizes(float minModel, float maxModel, int maxView) {
        return new LinearScale(minModel, (maxModel - minModel) / maxView);
    }
}
