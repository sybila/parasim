package org.sybila.parasim.extension.visualisation.projection.view.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JViewport;
import org.sybila.parasim.extension.visualisation.projection.view.display.rule.GraphicsStringMeasure;
import org.sybila.parasim.extension.visualisation.projection.view.display.rule.NaturalScaleFormat;
import org.sybila.parasim.extension.visualisation.projection.view.display.rule.ScaleFormat;
import org.sybila.parasim.extension.visualisation.projection.view.display.rule.StringMeasure;
import org.sybila.parasim.extension.visualisation.projection.view.display.rule.TickModel;
import org.sybila.parasim.extension.visualisation.projection.view.util.DimensionFunctional;
import org.sybila.parasim.extension.visualisation.projection.view.util.Orientation;
import org.sybila.parasim.extension.visualisation.projection.view.util.PositionChangeListener;
import org.sybila.parasim.visualisation.projection.api.scale.Scale;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SlidingRule extends JRootPane implements PositionChangeListener {

    private static final Dimension RULE_SIZE = new Dimension(70, 30);
    private static final int SHORT_TICK = 5;
    private static final int PADDING = 5;
    private static final int TICK_CAPTION_PADDING = 2;
    private static final int FONT_SIZE = 10;
    private static final Color BG_COLOR = Color.WHITE;
    private static final Color FG_COLOR = Color.BLACK;
    //
    private Orientation orientation;
    private Integer position = null;
    private JViewport content = new JViewport();
    private ScaleSource scaleSource;
    private Iterable<TickModel> ticks;
    private ScaleFormat scaleFormat = NaturalScaleFormat.getInstance();

    public SlidingRule(Orientation orientation, ScaleSource scaleSource) {
        if (orientation != Orientation.HORIZONTAL && orientation != Orientation.VERTICAL) {
            throw new IllegalArgumentException("Unsupported orientation: " + orientation.toString());
        }
        this.orientation = orientation;
        setPreferredSize(orientation.getDimensionFunctional().composeDimension(Integer.MAX_VALUE, orientation.other().getDimensionFunctional().get(RULE_SIZE)));

        if (scaleSource == null) {
            throw new IllegalArgumentException("Scale source of a rule cannot be null.");
        }
        this.scaleSource = scaleSource;

        setContentPane(content);
        content.setView(new JComponent() {

            @Override
            protected void paintComponent(Graphics g) {
                SlidingRule.this.paintScale((Graphics2D) g);
            }
        });

        setGlassPane(new JComponent() {

            @Override
            protected void paintComponent(Graphics g) {
                SlidingRule.this.paintPosition((Graphics2D) g);
            }
        });
        getGlassPane().setVisible(true);
    }

    public void updateView(int viewPosition) {
        updateViewImpl(viewPosition);
    }

    private void updateViewImpl(int viewPosition) {
        content.setViewPosition(orientation.getDimensionFunctional().composePoint(viewPosition, 0));
    }

    public void updateSize(int size, int viewPosition) {
        Scale scale = scaleSource.getScale(orientation);
        if (orientation == Orientation.HORIZONTAL) {
            ticks = scaleFormat.formatHorizontalScale(scale, size, new GraphicsStringMeasure((Graphics2D) getGraphics()));
        } else {
            ticks = scaleFormat.formatVerticalScale(scale, size, new GraphicsStringMeasure((Graphics2D) getGraphics()), content.getViewSize().width - 2 * PADDING - SHORT_TICK);
        }

        Dimension newSize = orientation.getDimensionFunctional().composeDimension(size, orientation.other().getDimensionFunctional().get(RULE_SIZE));
        content.getView().setPreferredSize(newSize);
        content.getView().setSize(newSize);
        updateViewImpl(viewPosition);
    }

    @Override
    public void positionChanged(Point position) {
        if (position == null) {
            this.position = null;
        } else {
            this.position = orientation.getDimensionFunctional().get(position);
        }
        getGlassPane().repaint();
    }

    private void paintPosition(Graphics2D canvas) {
        if (position != null) {
            Rectangle bounds = canvas.getClipBounds();

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
            canvas.setColor(FG_COLOR);
            canvas.fill(arrow);
        }
    }

    private void paintScale(Graphics2D canvas) {
        canvas.setColor(BG_COLOR);
        Dimension size = content.getViewSize();
        canvas.clearRect(0, 0, size.width, size.height);
        canvas.fill(new Rectangle(size));

        canvas.setColor(FG_COLOR);
        canvas.setFont(canvas.getFont().deriveFont(FONT_SIZE));

        StringMeasure measure = new GraphicsStringMeasure(canvas);
        DimensionFunctional df = orientation.getDimensionFunctional();
        for (TickModel tick : ticks) {
            Point start = new Point(df.get(tick.getPosition(), size.width), df.get(0, tick.getPosition()));
            if (tick.getCaption() == null) {
                canvas.drawLine(start.x, start.y, start.x - df.get(0, SHORT_TICK), start.y + df.get(SHORT_TICK, 0));
            } else {
                canvas.drawLine(start.x, start.y, start.x - df.get(0, size.width - SHORT_TICK), start.y + df.get(size.height - SHORT_TICK, 0));
                String caption = tick.getCaption();
                Dimension captionSize = measure.getStringBounds(caption);
                canvas.drawString(caption, start.x + df.get(TICK_CAPTION_PADDING, -PADDING - SHORT_TICK - captionSize.width), start.y + df.get(SHORT_TICK + PADDING + captionSize.height, -TICK_CAPTION_PADDING));
            }

        }
    }
}
