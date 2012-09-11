package org.sybila.parasim.extension.visualisation.projection.view.display.zoom;

import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Zoom {

    public Dimension getGraphSize();

    /**
     * Position of the middle of viewport.
     * @return
     */
    public Point getViewPosition();
}
