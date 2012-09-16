package org.sybila.parasim.extension.visualisation.projection.view.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PaddedPane extends JRootPane {

    private class Overlay extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            if (position != null) {
                int hPad = PaddedPane.this.content.getInsets().left;
                int vPad = PaddedPane.this.content.getInsets().top;
                Rectangle bounds = g.getClipBounds();
                g.setColor(guideColor);
                g.drawLine(position.x + hPad, bounds.y, position.x + hPad, bounds.y + bounds.height);
                g.drawLine(bounds.x, position.y + vPad, bounds.x + bounds.width, position.y + vPad);
            }
        }
    }
    private Color guideColor = Color.BLUE;
    private JComponent view;
    private JPanel content;
    private EmptyBorder padding;
    private MouseAdapter mouseTracker;
    private Point position = null;

    public PaddedPane(JComponent view) {
        this.view = view;
        content = new JPanel(new BorderLayout());
        setContentPane(content);
        content.add(view, BorderLayout.CENTER);
        setPadding(0, 0);
        setBackground(view.getBackground());

        setGlassPane(new Overlay());
        getGlassPane().setVisible(true);
        mouseTracker = new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseMove(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseMove(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseMove(null);
            }
        };
        view.addMouseListener(mouseTracker);
        view.addMouseMotionListener(mouseTracker);
    }

    private void mouseMove(Point position) {
        this.position = position;
        getGlassPane().repaint();
    }

    private void setPadding(int horizontal, int vertical) {
        padding = new EmptyBorder(vertical, horizontal, vertical, horizontal);
        content.setBorder(padding);
    }

    public void setExtraSpace(int horizontal, int vertical) {
        int left = horizontal / 2;
        int right = (horizontal % 2 == 0) ? left : left + 1;
        int top = vertical / 2;
        int bottom = (vertical % 2 == 0) ? top : top + 1;
        padding = new EmptyBorder(top, left, bottom, right);
        content.setBorder(padding);
        updateLayout();
    }

    private void updateLayout() {
        revalidate();
        setSize(getPreferredSize());
    }

    public void setCentralSize(Dimension size) {
        view.setPreferredSize(size);
        updateLayout();
    }
}
