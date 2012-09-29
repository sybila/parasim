package org.sybila.parasim.visualisation.projection.api.scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
@Deprecated //this kind of logarithmic scale is wrong
public class LogarithmicScale implements Scale {

    private double logBase, min;

    private LogarithmicScale(double logBase, float min) {
        this.logBase = logBase;
        this.min = min;
    }

    @Override
    public ScaleType getType() {
        return ScaleType.LOGARITHMIC;
    }

    @Override
    public float getModelCoordinate(int screenCoordinate) {
        return Double.valueOf(Math.expm1(screenCoordinate * logBase) + min).floatValue();
    }

    @Override
    public int getViewCoordinate(float modelCoordinate) {
        return Double.valueOf(Math.log1p(modelCoordinate - min) / logBase).intValue();
    }

    static Scale getFromSizes(float minModel, float maxModel, int maxView) {
        return new LogarithmicScale(Math.log1p(maxModel - minModel) / maxView, minModel);
    }
}
