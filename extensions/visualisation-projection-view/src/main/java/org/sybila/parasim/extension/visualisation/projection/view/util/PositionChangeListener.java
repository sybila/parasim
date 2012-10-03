package org.sybila.parasim.extension.visualisation.projection.view.util;

import java.awt.Point;
import java.util.EventListener;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface PositionChangeListener extends EventListener {

    public void positionChanged(Point position);
}
