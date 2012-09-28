package org.sybila.parasim.visualisation.projection.api.scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class InverseScale implements Scale {

    private Scale src;
    private int size;

    private InverseScale(Scale source, int size) {
        src = source;
        this.size = size;
    }

    @Override
    public ScaleType getType() {
        return src.getType();
    }

    @Override
    public float getModelCoordinate(int screenCoordinate) {
        return src.getModelCoordinate(size - screenCoordinate);
    }

    @Override
    public int getViewCoordinate(float modelCoordinate) {
        return size - src.getViewCoordinate(modelCoordinate);
    }

    public static Scale getFromSize(Scale compomentScale, int componentSize) {
        return new InverseScale(compomentScale, componentSize);
    }
}
