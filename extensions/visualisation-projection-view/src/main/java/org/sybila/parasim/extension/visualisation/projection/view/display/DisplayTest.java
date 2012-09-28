package org.sybila.parasim.extension.visualisation.projection.view.display;

import org.sybila.parasim.extension.visualisation.projection.view.display.zoom.FillingConservativeZoom;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import org.sybila.parasim.extension.visualisation.projection.view.display.util.DimensionFunctional;
import org.sybila.parasim.extension.visualisation.projection.view.display.util.Orientation;
import org.sybila.parasim.visualisation.projection.api.scale.InverseScale;
import org.sybila.parasim.visualisation.projection.api.scale.Scale;
import org.sybila.parasim.visualisation.projection.api.scale.ScaleType;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class DisplayTest extends JFrame {

    private static class ViewPanel extends JPanel implements ScaleSource {

        private static final Rectangle MODEL_BOUNDS = new Rectangle(-20, -20, 100, 100);

        private void halveRectangle(Graphics g, Rectangle rect, int depth, int baseDepth) {
            if (depth > 0) {
                g.setColor(new Color(depth / (float) baseDepth, 0f, 0f));
                //if (rect.width > rect.height) {
                if (depth % 2 == 0) {
                    int halfWidth = rect.width / 2;
                    g.fillRect(rect.x + halfWidth, rect.y, rect.width - halfWidth, rect.height);
                    halveRectangle(g, new Rectangle(rect.x, rect.y, halfWidth, rect.height), depth - 1, baseDepth);
                } else {
                    int halfHeight = rect.height / 2;
                    g.fillRect(rect.x, rect.y, rect.width, rect.height - halfHeight);
                    halveRectangle(g, new Rectangle(rect.x, rect.y + halfHeight, rect.width, halfHeight), depth - 1, baseDepth);
                }
            } else {
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
        }

        @Override
        public void paint(Graphics g) {
            g.clearRect(0, 0, getWidth(), getHeight());
            halveRectangle(g, new Rectangle(new Point(), getSize()), 10, 10);
        }

        @Override
        public Scale getScale(Orientation orientation) {
            DimensionFunctional dim = orientation.getDimensionFunctional();
            Scale result = ScaleType.LINEAR.getFromSizes(dim.get(MODEL_BOUNDS.getLocation()), dim.get(MODEL_BOUNDS.getSize()), dim.get(getSize()));
            if (orientation == Orientation.HORIZONTAL) {
                return result;
            } else {
                assert orientation == Orientation.VERTICAL;
                return InverseScale.getFromSize(result, dim.get(getSize()));
            }
        }
    }
    private Display display;

    public DisplayTest() {
        setTitle("Test Display");
        setLocation(250, 150);
        setSize(new Dimension(750, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());

        // set up display //
        ViewPanel view = new ViewPanel();
        view.setBorder(new LineBorder(Color.BLACK));
        display = new Display(view, FillingConservativeZoom.getInstance(), view);
        add(display);

        // set up menus //
        setJMenuBar(new JMenuBar());
        JMenu zoom, submenu;
        JMenuItem item;
        //zoom
        zoom = new JMenu("Zoom");
        zoom.setMnemonic(KeyEvent.VK_Z);
        item = new JMenuItem(display.getAction(Display.Actions.ZOOM_IN_BOTH));
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, KeyEvent.CTRL_DOWN_MASK));
        zoom.add(item);
        item = new JMenuItem(display.getAction(Display.Actions.ZOOM_OUT_BOTH));
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, KeyEvent.CTRL_DOWN_MASK));
        zoom.add(item);
        item = new JMenuItem(display.getAction(Display.Actions.ZOOM_FILL));
        zoom.add(item);
        zoom.addSeparator();
        // horizontal zoom //
        submenu = new JMenu("Horizontal Zoom");
        item = new JMenuItem(display.getAction(Display.Actions.ZOOM_IN_HORIZONTAL));
        item.setText("Zoom In");
        submenu.add(item);
        item = new JMenuItem(display.getAction(Display.Actions.ZOOM_OUT_HORIZONTAL));
        item.setText("Zoom out");
        submenu.add(item);
        zoom.add(submenu);
        // vertical zoom //
        submenu = new JMenu("Vertical Zoom");
        item = new JMenuItem(display.getAction(Display.Actions.ZOOM_IN_VERTICAL));
        item.setText("Zoom in");
        submenu.add(item);
        item = new JMenuItem(display.getAction(Display.Actions.ZOOM_OUT_VERTICAL));
        item.setText("Zoom out");
        submenu.add(item);
        zoom.add(submenu);
        getJMenuBar().add(zoom);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame frame = new DisplayTest();
                frame.setVisible(true);
            }
        });

    }
}
