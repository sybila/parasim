package org.sybila.parasim.model.verification.result;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.xml.XMLRepresentable;

/**
 * Result of a verification -- list of points with associated property robustness value.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface VerificationResult extends XMLRepresentable {
    
    /**
     * Return number of points with robustness values.
     * @return Number of points.
     */
    public int size();
    
    /**
     * Return coordinates of a given point.
     * @param index Index of specified point.
     * @return Coordinates of point with specified index.
     */
    public Point getPoint(int index);
    
    //public boolean getValidity(int index);
    
    /**
     * Return property robustness value in a specified point.
     * @param index Index of specified point.
     * @return Property robustness value in point with given index.
     */
    public float getRobustness(int index); //postupem času přepsat na něco jiného než float
}
