package org.sybila.parasim.extension.visualisation.projection.view.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import javax.swing.JComponent;
import org.sybila.parasim.extension.visualisation.projection.view.display.util.Orientation;
import org.sybila.parasim.extension.visualisation.projection.view.display.util.PositionChangeListener;
import org.sybila.parasim.visualisation.projection.api.scale.Scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
@Deprecated
public class Rule extends JComponent implements PositionChangeListener {

    private static final int SHORT_TICK = 5;
    private static final int PADDING = 2;
    private static final int TICK_SPACING = 5;
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.##E0");
    private static final float TEST_NUMBER = -1.2345e-32f;
    private static final int FONT_SIZE = 10;
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color FG_COLOR = Color.BLACK;
    //
    private Orientation orientation;
    private Integer position;
    private boolean initialized = false;
    private Dimension legendSize;
    private ScaleSource scaleSource;
    private Scale scale = null;
    private int viewPos;

    public Rule(Orientation orientation, ScaleSource scaleSource) {
        if (orientation != Orientation.HORIZONTAL && orientation != Orientation.VERTICAL) {
            throw new IllegalArgumentException("Unknown orientation: " + orientation.toString());
        }
        this.orientation = orientation;
        if (scaleSource == null) {
            throw new IllegalArgumentException("Argument scaleSource is null.");
        }
        this.scaleSource = scaleSource;
    }

    public void updatePosition(Point position) {
        if (position == null) {
            this.position = null;
        } else {
            this.position = orientation.getDimensionFunctional().get(position);
        }
        repaint();
    }

    public void updateScale(int viewPosition) {
        scale = scaleSource.getScale(orientation);
        updateViewImpl(viewPosition);
    }

    public void updateView(int viewPosition) {
        updateViewImpl(viewPosition);
    }

    private void updateViewImpl(int viewPosition) {
        viewPos = viewPosition;
        repaint();
    }

    @Override
    public void positionChanged(Point position) {
        updatePosition(position);
    }

    private String formatNumber(float number) {
        return NUMBER_FORMAT.format(number);
    }

    private void initialize(Graphics2D g) {
        legendSize = getStringBounds(g, formatNumber(TEST_NUMBER)).getSize();
        int dim = orientation.other().getDimensionFunctional().get(legendSize) + 2 * PADDING + SHORT_TICK;
        if (orientation == Orientation.HORIZONTAL) {
            setPreferredSize(new Dimension(Integer.MAX_VALUE, dim));
        } else {
            assert (orientation == Orientation.VERTICAL);
            setPreferredSize(new Dimension(dim, Integer.MAX_VALUE));
        }
        initialized = true;
    }

    private Rectangle getStringBounds(Graphics2D g, String s) {
        return g.getFont().getStringBounds(s, g.getFontRenderContext()).getBounds();
    }

    private void paintPosition(Graphics2D canvas, Rectangle bounds) {
        if (position != null) {
            Polygon arrow = new Polygon();
            if (orientation == Orientation.HORIZONTAL) {
                arrow.addPoint(position, 0);
                arrow.addPoint(position - SHORT_TICK, SHORT_TICK);
                arrow.addPoint(position + SHORT_TICK, SHORT_TICK);
            } else {
                assert (orientation == Orientation.VERTICAL);
                arrow.addPoint(bounds.width, position);
                arrow.addPoint(bounds.width - SHORT_TICK, position - SHORT_TICK);
                arrow.addPoint(bounds.width - SHORT_TICK, position + SHORT_TICK);
            }
            canvas.fill(arrow);

            String value = formatNumber(scale.getModelCoordinate(position + viewPos));
            Rectangle valBounds = getStringBounds(canvas, value);
            Dimension valDim = new Dimension(valBounds.width, -valBounds.y);
            int valX, valY;
            if (orientation == Orientation.HORIZONTAL) {
                valX = position - valDim.width / 2;
                if (valX < 0) {
                    valX = 0;
                } else if (valX + valDim.width > bounds.width) {
                    valX = bounds.width - valDim.width;
                }
                valY = SHORT_TICK + PADDING + valDim.height;
            } else {
                assert orientation == Orientation.VERTICAL;
                valY = position - valBounds.y / 2;
                if (valY < valDim.height) {
                    valY = valDim.height;
                } else if (valY > bounds.height) {
                    valY = bounds.height;
                }
                valX = bounds.width - SHORT_TICK - PADDING - valDim.width;
            }
            canvas.setColor(BG_COLOR);
            canvas.fill(new Rectangle(valX - PADDING, valY - valDim.height - PADDING, valDim.width + 2 * PADDING, valDim.height + 2 * PADDING));
            canvas.setColor(FG_COLOR);
            canvas.drawString(value, valX, valY);
        }
    }

    private void paintTicks(Graphics2D canvas, Rectangle bounds) {
        if (orientation == Orientation.HORIZONTAL) {
            canvas.drawLine(0, 0, 0, 10);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D canvas = (Graphics2D) g;
        canvas.setFont(canvas.getFont().deriveFont((float) FONT_SIZE));
        if (!initialized) {
            initialize(canvas);
            return;
        }
        Rectangle bounds = canvas.getClipBounds();
        canvas.clearRect(bounds.x, bounds.y, bounds.width, bounds.height);
        if (isOpaque()) {
            canvas.setColor(BG_COLOR);
            canvas.fill(bounds);
        }
        canvas.setColor(FG_COLOR);

        if (scale != null) {
            paintPosition(canvas, bounds);
            paintTicks(canvas, bounds);
        }
    }
}
