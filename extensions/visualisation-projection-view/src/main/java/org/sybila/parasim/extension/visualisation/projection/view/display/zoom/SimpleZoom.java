package org.sybila.parasim.extension.visualisation.projection.view.display.zoom;

import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SimpleZoom implements Zoom {

    final private Dimension graph;
    final private Point pos;

    public SimpleZoom(Dimension graphSize, Point viewPosition) {
        graph = graphSize;
        pos = viewPosition;
    }

    @Override
    public Dimension getGraphSize() {
        return graph;
    }

    @Override
    public Point getViewPosition() {
        return pos;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Zoom)) {
            return false;
        }
        Zoom target = (Zoom) obj;
        return getGraphSize().equals(target.getGraphSize()) && getViewPosition().equals(target.getViewPosition());
    }

    @Override
    public int hashCode() {
        return 47 * getGraphSize().hashCode() + getViewPosition().hashCode();
    }
}
