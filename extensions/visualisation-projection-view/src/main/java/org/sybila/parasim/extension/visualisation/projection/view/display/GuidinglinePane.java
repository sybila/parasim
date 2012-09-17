package org.sybila.parasim.extension.visualisation.projection.view.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JRootPane;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class GuidinglinePane extends JRootPane {

    private Color guideColor = Color.BLUE;
    private JComponent view = null;
    private Point position = null;
    private MouseAdapter mouseTracker;

    public GuidinglinePane(JComponent content) {
        setContentPane(content);
        mouseTracker = new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                moveMouse(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                moveMouse(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setPosition(null);
            }
        };
        setGlassPane(new JComponent() {

            @Override
            protected void paintComponent(Graphics g) {
                if (position != null) {
                    Rectangle bounds = g.getClipBounds();
                    g.setColor(guideColor);
                    g.drawLine(position.x, bounds.y, position.x, bounds.y + bounds.height);
                    g.drawLine(bounds.x, position.y, bounds.x + bounds.width, position.y);;
                }
            }
        });
        getGlassPane().setVisible(true);
    }

    private void setPosition(Point p) {
        position = p;
        getGlassPane().repaint();
    }

    private void moveMouse(MouseEvent me) {
        Point eventLocation = me.getLocationOnScreen();
        Point reference = getGlassPane().getLocationOnScreen();
        int x = eventLocation.x - reference.x;
        int y = eventLocation.y - reference.y;
        if ((x < 0) || (y < 0) || (x >= getGlassPane().getWidth()) || (y >= getGlassPane().getHeight())) {
            setPosition(null);
        } else {
            setPosition(new Point(x, y));
        }
    }

    public void setView(JComponent newView) {
        if (view != null) {
            view.removeMouseListener(mouseTracker);
            view.removeMouseMotionListener(mouseTracker);
        }
        view = newView;
        view.addMouseListener(mouseTracker);
        view.addMouseMotionListener(mouseTracker);
    }
}
