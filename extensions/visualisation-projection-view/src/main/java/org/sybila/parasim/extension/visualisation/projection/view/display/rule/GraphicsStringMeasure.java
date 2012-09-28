package org.sybila.parasim.extension.visualisation.projection.view.display.rule;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class GraphicsStringMeasure implements StringMeasure {

    private Graphics2D g;

    public GraphicsStringMeasure(Graphics2D graphics) {
        if (graphics == null) {
            throw new IllegalArgumentException("Graphics cannot be null.");
        }
        g = graphics;
    }

    @Override
    public Dimension getStringBounds(String target) {
        Rectangle bounds = g.getFont().getStringBounds(target, g.getFontRenderContext()).getBounds();
        return new Dimension(bounds.width, -bounds.y);
    }
}
