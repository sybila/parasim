package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class CanvasPane extends JRootPane {

    private static final Color GUIDE_COLOR = Color.BLUE;
    private Point position;

    private class Overlay extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            Rectangle bounds = g.getClipBounds();

            if (position != null) {
                g.setColor(GUIDE_COLOR);
                g.drawLine(position.x, bounds.y, position.x, bounds.y + bounds.height);
                g.drawLine(bounds.x, position.y, bounds.x + bounds.width, position.y);
            }
        }
    }

    public CanvasPane(ResultPlotterConfiguration conf, Canvas canvas) {
        setContentPane(canvas);

        setGlassPane(new Overlay());
        getGlassPane().setVisible(conf.getShowGuides());

        MouseAdapter mouseActions = new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                position = e.getPoint();
                getGlassPane().repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                position = null;
                getGlassPane().repaint();
            }
        };
        addMouseListener(mouseActions);
        addMouseMotionListener(mouseActions);
    }
}
