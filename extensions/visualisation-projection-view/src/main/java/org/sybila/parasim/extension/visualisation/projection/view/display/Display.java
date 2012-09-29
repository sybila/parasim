package org.sybila.parasim.extension.visualisation.projection.view.display;

import org.sybila.parasim.extension.visualisation.projection.view.display.zoom.SimpleZoom;
import org.sybila.parasim.extension.visualisation.projection.view.display.zoom.ZoomBehaviour;
import org.sybila.parasim.extension.visualisation.projection.view.display.zoom.Zoom;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.EnumMap;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import org.sybila.parasim.extension.visualisation.projection.view.display.util.DimensionFunctional;
import org.sybila.parasim.extension.visualisation.projection.view.display.util.Orientation;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Display extends JPanel {

    private static final double ZOOM_FACTOR = 0.1;
    private static final double ZOOM_IN_FACTOR = 1 + ZOOM_FACTOR;
    private static final double ZOOM_OUT_FACTOR = 1 / ZOOM_IN_FACTOR;
    //
    private static ResourceBundle STRINGS = ResourceBundle.getBundle(Display.class.getName());

    public static enum Actions {

        ZOOM_IN_VERTICAL,
        ZOOM_IN_HORIZONTAL,
        ZOOM_IN_BOTH,
        ZOOM_OUT_VERTICAL,
        ZOOM_OUT_HORIZONTAL,
        ZOOM_OUT_BOTH,
        ZOOM_FILL;
    }

    private static final class Constraints {

        public static final GridBagConstraints HSCROLL;
        public static final GridBagConstraints VSCROLL;
        public static final GridBagConstraints HRULE;
        public static final GridBagConstraints VRULE;
        public static final GridBagConstraints VIEWPORT;

        static {
            HSCROLL = CrossGridBagConstraints.getNorth();
            HSCROLL.fill = GridBagConstraints.HORIZONTAL;
            VSCROLL = CrossGridBagConstraints.getEast();
            VSCROLL.fill = GridBagConstraints.VERTICAL;
            HRULE = CrossGridBagConstraints.getSouth();
            HRULE.fill = GridBagConstraints.HORIZONTAL;
            VRULE = CrossGridBagConstraints.getWest();
            VRULE.fill = GridBagConstraints.VERTICAL;
            VIEWPORT = CrossGridBagConstraints.getCenter();
            VIEWPORT.fill = GridBagConstraints.BOTH;
            VIEWPORT.weightx = 1;
            VIEWPORT.weighty = 1;
        }
    }
    private JScrollBar hScroll, vScroll;
    private SlidingRule hRule, vRule;
    private JViewport viewport;
    private PaddedPane viewPanel;
    private GuidinglinePane guidingPane;
    //
    private Zoom zoom = null;
    private ZoomBehaviour zoomBehaviour;
    private EnumMap<Actions, Action> actions = new EnumMap(Actions.class);
    private ComponentListener resizeListener = new ComponentAdapter() {

        @Override
        public void componentResized(ComponentEvent e) {
            if (zoom == null) {
                getAction(Actions.ZOOM_FILL).actionPerformed(new ActionEvent(Display.this, Actions.ZOOM_FILL.ordinal(), Actions.ZOOM_FILL.toString()));
            } else {
                zoom = zoomBehaviour.resize(zoom, getViewportSize());
                updateZoom();
            }
        }
    };
    private AdjustmentListener scrollListener = new AdjustmentListener() {

        @Override
        public void adjustmentValueChanged(AdjustmentEvent ae) {
            int viewX = hScroll.getValue();
            int viewY = vScroll.getValue();
            viewport.setViewPosition(new Point(viewX, viewY));
            hRule.updateView(viewX);
            vRule.updateView(viewY);
            viewX += getViewportSize().width / 2;
            viewY += getViewportSize().height / 2;
            zoom = new SimpleZoom(zoom.getGraphSize(), new Point(viewX, viewY));
        }
    };
    private MouseWheelListener wheelListener = new MouseWheelListener() {

        @Override
        public void mouseWheelMoved(MouseWheelEvent mwe) {
            if (mwe.isControlDown()) {
                // zoom //
                Actions action = null;
                if (mwe.isAltDown()) {
                    // zoom horizontally //
                    if (mwe.getWheelRotation() > 0) {
                        action = Actions.ZOOM_OUT_HORIZONTAL;
                    } else {
                        action = Actions.ZOOM_IN_HORIZONTAL;
                    }
                } else if (mwe.isShiftDown()) {
                    // zoom vertically //
                    if (mwe.getWheelRotation() > 0) {
                        action = Actions.ZOOM_OUT_VERTICAL;
                    } else {
                        action = Actions.ZOOM_IN_VERTICAL;
                    }
                } else {
                    // zoom in both direction //
                    if (mwe.getWheelRotation() > 0) {
                        action = Actions.ZOOM_OUT_BOTH;
                    } else {
                        action = Actions.ZOOM_IN_BOTH;
                    }
                }
                getAction(action).actionPerformed(new ActionEvent(viewport, action.ordinal(), action.toString()));
            } else {
                // only move //
                if (mwe.isAltDown()) {
                    // move horizontally //
                    hScroll.setValue(hScroll.getValue() - mwe.getWheelRotation() * hScroll.getUnitIncrement());
                } else {
                    // move vertically //
                    vScroll.setValue(vScroll.getValue() + mwe.getWheelRotation() * vScroll.getUnitIncrement());
                }
            }
        }
    };
    private GuidinglinePane.ZoomTarget zoomTarget = new GuidinglinePane.ZoomTarget() {

        @Override
        public void zoomToRectangle(Rectangle target) {
            zoom = zoomBehaviour.zoomToRectangle(zoom, target, getViewportSize());
            updateZoom();
        }
    };

    public Display(JComponent view, ZoomBehaviour zoomBehaviour, ScaleSource scaleSource) {
        this.zoomBehaviour = zoomBehaviour;
        setLayout(new GridBagLayout());

        hScroll = new JScrollBar(JScrollBar.HORIZONTAL);
        hScroll.addAdjustmentListener(scrollListener);
        add(hScroll, Constraints.HSCROLL);

        vScroll = new JScrollBar(JScrollBar.VERTICAL);
        vScroll.addAdjustmentListener(scrollListener);
        add(vScroll, Constraints.VSCROLL);

        hRule = new SlidingRule(Orientation.HORIZONTAL, scaleSource);
        hRule.setOpaque(true);
        add(hRule, Constraints.HRULE);

        vRule = new SlidingRule(Orientation.VERTICAL, scaleSource);
        vRule.setOpaque(true);
        add(vRule, Constraints.VRULE);

        viewport = new JViewport();
        viewport.addMouseWheelListener(wheelListener);
        guidingPane = new GuidinglinePane(viewport, zoomTarget);
        guidingPane.setView(view);
        guidingPane.addPositionChangeListener(hRule);
        guidingPane.addPositionChangeListener(vRule);
        guidingPane.setZoomToRectangleActive(false);
        add(guidingPane, Constraints.VIEWPORT);

        viewPanel = new PaddedPane(view);
        viewport.setView(viewPanel);

        addComponentListener(resizeListener);
        actions.put(Actions.ZOOM_IN_HORIZONTAL, new AbstractAction(STRINGS.getString(Actions.ZOOM_IN_HORIZONTAL.toString())) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                zoom = Display.this.zoomBehaviour.horizontalZoom(zoom, ZOOM_IN_FACTOR, getViewportSize());
                updateZoom();
            }
        });
        actions.put(Actions.ZOOM_IN_VERTICAL, new AbstractAction(STRINGS.getString(Actions.ZOOM_IN_VERTICAL.toString())) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                zoom = Display.this.zoomBehaviour.verticalZoom(zoom, ZOOM_IN_FACTOR, getViewportSize());
                updateZoom();
            }
        });
        actions.put(Actions.ZOOM_IN_BOTH, new AbstractAction(STRINGS.getString(Actions.ZOOM_IN_BOTH.toString())) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                zoom = Display.this.zoomBehaviour.zoom(zoom, ZOOM_IN_FACTOR, getViewportSize());
                updateZoom();
            }
        });
        actions.put(Actions.ZOOM_OUT_HORIZONTAL, new AbstractAction(STRINGS.getString(Actions.ZOOM_OUT_HORIZONTAL.toString())) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                zoom = Display.this.zoomBehaviour.horizontalZoom(zoom, ZOOM_OUT_FACTOR, getViewportSize());
                updateZoom();
            }
        });
        actions.put(Actions.ZOOM_OUT_VERTICAL, new AbstractAction(STRINGS.getString(Actions.ZOOM_OUT_VERTICAL.toString())) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                zoom = Display.this.zoomBehaviour.verticalZoom(zoom, ZOOM_OUT_FACTOR, getViewportSize());
                updateZoom();
            }
        });
        actions.put(Actions.ZOOM_OUT_BOTH, new AbstractAction(STRINGS.getString(Actions.ZOOM_OUT_BOTH.toString())) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                zoom = Display.this.zoomBehaviour.zoom(zoom, ZOOM_OUT_FACTOR, getViewportSize());
                updateZoom();
            }
        });
        actions.put(Actions.ZOOM_FILL, new AbstractAction(STRINGS.getString(Actions.ZOOM_FILL.toString())) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Dimension extent = getViewportSize();
                zoom = new SimpleZoom(new Dimension(extent.width, extent.height), new Point(extent.width / 2, extent.height / 2));
                updateZoom();
            }
        });
    }

    private Dimension getViewportSize() {
        return viewport.getExtentSize();
    }

    private int checkPosition(int position, DimensionFunctional dimension) {
        int viewPanelDim = dimension.get(viewPanel.getSize());
        int viewportDim = dimension.get(viewport.getSize());
        if (viewPanelDim < viewportDim) {
            throw new IllegalStateException(dimension.getDimensionString() + " of viewPanel is smaller than " + dimension.getDimensionString() + " of viewport.");
        } else if (viewPanelDim == viewportDim) {
            return 0;
        } else {
            if (position < 0) {
                return 0;
            } else if (position > viewPanelDim - viewportDim) {
                return viewPanelDim - viewportDim;
            } else {
                return position;
            }
        }
    }

    private void updateZoom() {
        viewPanel.setCentralSize(zoom.getGraphSize());
        Dimension extent = getViewportSize();
        int hPad = Math.max(extent.width - zoom.getGraphSize().width, 0);
        int vPad = Math.max(extent.height - zoom.getGraphSize().height, 0);
        viewPanel.setExtraSpace(hPad, vPad);

        int viewX = checkPosition(zoom.getViewPosition().x - (extent.width / 2 - hPad / 2), DimensionFunctional.X);
        int viewY = checkPosition(zoom.getViewPosition().y - (extent.height / 2 - vPad / 2), DimensionFunctional.Y);
        viewport.setViewPosition(new Point(viewX, viewY));

        // scroll bars //
        hScroll.setModel(new DefaultBoundedRangeModel(viewX, extent.width, 0, viewPanel.getWidth()));
        vScroll.setModel(new DefaultBoundedRangeModel(viewY, extent.height, 0, viewPanel.getHeight()));

        // rules //
        hRule.updateSize(zoom.getGraphSize().width, viewX);
        vRule.updateSize(zoom.getGraphSize().height, viewY);
    }

    public Action getAction(Actions type) {
        return actions.get(type);
    }

    public void setZoomToRectangleActive(boolean activity) {
        guidingPane.setZoomToRectangleActive(activity);
    }

    public boolean getZoomToRectangleActive() {
        return guidingPane.getZoomToRectangleActive();
    }
}
