package org.sybila.parasim.visualisation.projection.api.scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Scale {

    public ScaleType getType();

    public float getModelCoordinate(int screenCoordinate);

    public int getViewCoordinate(float modelCoordinate);
}
