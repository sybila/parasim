package org.sybila.parasim.extension.visualisation.projection.view.display.zoom;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ZoomBehaviour {

    public Zoom resize(Zoom source, Dimension viewportSize);

    public Zoom verticalZoom(Zoom source, double factor, Dimension viewportSize);

    public Zoom horizontalZoom(Zoom source, double factor, Dimension viewportSize);

    public Zoom zoom(Zoom source, double factor, Dimension viewportSize);

    public Zoom zoomToRectangle(Zoom source, Rectangle target, Dimension viewportSize);
}
