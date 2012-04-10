package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 * Draws points on a 2D canvas. Handles coordinate transformation and resizing.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Canvas extends JPanel {

    protected static final Color BLANK = Color.WHITE;
    private Point2DLayer points = null;

    @Override
    public void paint(Graphics g) {
        // clean //
        Graphics2D canvas = (Graphics2D) g;
        canvas.setBackground(BLANK);
        canvas.clearRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * Designates points to be rendered. Forces repaint.
     * @param layer Rendered points.
     */
    public void setPoints(Point2DLayer layer) {
        points = layer;
    }
    
}
