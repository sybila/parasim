package org.sybila.parasim.visualisation.projection.api.points;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface LayerSpecification {

    public int getDimension();

    public int getXDimension();

    public int getYDimension();

    public int getLayer(int index);
}
