package org.sybila.parasim.extension.visualisation.projection.view.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.event.EventListenerList;
import org.sybila.parasim.extension.visualisation.projection.view.util.PositionChangeListener;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class GuidinglinePane extends JRootPane {

    public static interface ZoomTarget {

        public void zoomToRectangle(Rectangle target);
    }
    //
    private Color guideColor = Color.BLUE;
    private Color rectangleColor = Color.BLUE;
    private Color rectangleFillColor = new Color(0, 0, 255, 125);
    //
    private JComponent view = null;
    private Point position = null;
    private MouseAdapter mouseTracker;
    private EventListenerList listeners = new EventListenerList();
    //
    private boolean zoomRectangleActive = true;
    private Point dragStart = null;
    private Point relativeDragStart, dragPos;
    private ZoomTarget zoomTarget;

    public GuidinglinePane(JComponent content, ZoomTarget zoomTarget) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null.");
        }
        if (zoomTarget == null) {
            throw new IllegalArgumentException("Zoom target cannot be null.");
        }
        this.zoomTarget = zoomTarget;
        setContentPane(content);
        mouseTracker = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (zoomRectangleActive) {
                    dragStart = e.getPoint();
                    relativeDragStart = relativize(e.getLocationOnScreen());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (zoomRectangleActive) {
                    Point dragEnd = e.getPoint();
                    if (!dragStart.equals(dragEnd)) {
                        zoomToRectangle(dragStart, dragEnd);
                    }
                    dragStart = null;
                    getGlassPane().repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                moveMouse(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (zoomRectangleActive) {
                    dragPos = relativize(e.getLocationOnScreen());
                }
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
                if (dragStart != null) {
                    Rectangle rect = getRectangle(relativeDragStart, dragPos);
                    g.setColor(rectangleColor);
                    g.drawRect(rect.x, rect.y, rect.width, rect.height);
                    g.setColor(rectangleFillColor);
                    g.fillRect(rect.x, rect.y, rect.width, rect.height);
                }
            }
        });
        getGlassPane().setVisible(true);
    }

    private void setPosition(Point p) {
        position = p;
        getGlassPane().repaint();
        for (PositionChangeListener listener : listeners.getListeners(PositionChangeListener.class)) {
            listener.positionChanged(position);
        }
    }

    private Point relativize(Point onScreen) {
        Point reference = getGlassPane().getLocationOnScreen();
        int x = onScreen.x - reference.x;
        int y = onScreen.y - reference.y;
        return new Point(x, y);
    }

    private Point checkPoint(Point relative) {
        if ((relative.x < 0) || (relative.y < 0) || (relative.x >= getGlassPane().getWidth()) || (relative.y >= getGlassPane().getHeight())) {
            return null;
        } else {
            return relative;
        }
    }

    private void moveMouse(MouseEvent me) {
        setPosition(checkPoint(relativize(me.getLocationOnScreen())));
    }

    private void zoomToRectangle(Point start, Point end) {
        Rectangle target = getRectangle(start, end);
        zoomTarget.zoomToRectangle(target);
    }

    private Rectangle getRectangle(Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.abs(end.x - start.x);
        int height = Math.abs(end.y - start.y);
        return new Rectangle(x, y, width, height);
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

    public void addPositionChangeListener(PositionChangeListener listener) {
        listeners.add(PositionChangeListener.class, listener);
    }

    public void removePositionChangeListener(PositionChangeListener listener) {
        listeners.remove(PositionChangeListener.class, listener);
    }

    public PositionChangeListener[] getPositionChangeListeners() {
        return listeners.getListeners(PositionChangeListener.class);
    }

    public void setZoomToRectangleActive(boolean activity) {
        if (zoomRectangleActive == true && activity == false && dragStart != null) {
            dragStart = null;
            getGlassPane().repaint();
        }
        zoomRectangleActive = activity;
    }

    public boolean getZoomToRectangleActive() {
        return zoomRectangleActive;
    }
}
