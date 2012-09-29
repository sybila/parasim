package org.sybila.parasim.visualisation.projection.api.scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class NaturalLogScale implements Scale {

    private Scale linScale;

    private NaturalLogScale(Scale innner) {
        linScale = innner;
    }

    @Override
    public ScaleType getType() {
        return ScaleType.LOGARITHMIC;
    }

    @Override
    public float getModelCoordinate(int screenCoordinate) {
        return Double.valueOf(Math.pow(10, linScale.getModelCoordinate(screenCoordinate))).floatValue();
    }

    @Override
    public int getViewCoordinate(float modelCoordinate) {
        return linScale.getViewCoordinate(Double.valueOf(Math.log10(modelCoordinate)).floatValue());
    }

    public static Scale getFromSizes(float minModel, float maxModel, int size) {
        if (minModel <= 0 || maxModel <= 0) {
            throw new IllegalArgumentException("Both bounds of a logarithmic scale must be positive.");
        }
        return new NaturalLogScale(LinearScale.getFromSizes(Double.valueOf(Math.log10(minModel)).floatValue(), Double.valueOf(Math.log10(maxModel)).floatValue(), size));
    }
}
