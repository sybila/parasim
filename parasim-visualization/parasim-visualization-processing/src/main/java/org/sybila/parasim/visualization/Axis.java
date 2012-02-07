package org.sybila.parasim.visualization;

/**
 * Represents one axis of a multi-dimensional vizualization.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface Axis
{    
    String getName();

    void setName(String name);

    float getZoom();

    float setZoom(float zoom);

    ScaleType getScaleType();

    void setScaleType(ScaleType type);

    /**
     * Transforms a point's value to a coordinate acording to possible
     * translations, rotations or zooming.
     *
     * @param value Real world value to be transformed to viz
     * @return
     */
    float pointValueToCoordinate(float value);

    


}
