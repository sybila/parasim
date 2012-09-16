package org.sybila.parasim.extension.visualisation.projection.view.display.zoom;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum FillingConservativeZoom implements ZoomBehaviour {

    INSTANCE;

    private static int round(double d) {
        return Double.valueOf(d).intValue();
    }

    private static int checkPosition(int position, int graphSize, int viewportSize) {
        if (graphSize < viewportSize) {
            throw new IllegalArgumentException("Graph size is expected to be greater than viewport size.");
        }
        if (position < viewportSize / 2) {
            //System.out.println("too low a position: " + position + " < " + viewportSize / 2);
            return viewportSize / 2;
        }
        if (position > graphSize - viewportSize / 2) {
            //System.out.println("too high a position: " + position + " > " + (graphSize - viewportSize / 2));
            return graphSize - viewportSize / 2;
        }
        return position;
        //tady je někde chyba
    }

    @Override
    public Zoom resize(Zoom source, Dimension viewportSize) {
        int newX = source.getViewPosition().x;
        int newY = source.getViewPosition().y;
        int newWidth = source.getGraphSize().width;
        int newHeight = source.getGraphSize().height;
        return zoom(newX, newY, newWidth, newHeight, viewportSize);
    }

    @Override
    public Zoom horizontalZoom(Zoom source, double factor, Dimension viewportSize) {
        int newX = round(source.getViewPosition().x * factor);
        int newY = source.getViewPosition().y;
        int newWidth = round(source.getGraphSize().width * factor);
        int newHeight = source.getGraphSize().height;

        if (newWidth < viewportSize.width) {
            if (newHeight < viewportSize.height) {
                newWidth = viewportSize.width;
            }
            newX = newWidth / 2;
        } else {
            newX = checkPosition(newX, newWidth, viewportSize.width);
        }
        if (newHeight < viewportSize.height) {
            newY = newHeight / 2;
        } else {
            newY = checkPosition(newY, newHeight, viewportSize.height);
        }

        return new SimpleZoom(new Dimension(newWidth, newHeight), new Point(newX, newY));
    }

    @Override
    public Zoom verticalZoom(Zoom source, double factor, Dimension viewportSize) {
        int newX = source.getViewPosition().x;
        int newY = round(source.getViewPosition().y * factor);
        int newWidth = source.getGraphSize().width;
        int newHeight = round(source.getGraphSize().height * factor);
        if (newHeight < viewportSize.height) {
            if (newWidth < viewportSize.width) {
                newHeight = viewportSize.height;
            }
            newY = newHeight / 2;
        } else {
            newY = checkPosition(newY, newHeight, viewportSize.height);
        }
        if (newWidth < viewportSize.width) {
            newX = newWidth / 2;
        } else {
            newX = checkPosition(newX, newWidth, viewportSize.width);
        }

        return new SimpleZoom(new Dimension(newWidth, newHeight), new Point(newX, newY));
    }

    private Zoom zoom(int newX, int newY, int newWidth, int newHeight, Dimension viewportSize) {
        if (newWidth < viewportSize.width && newHeight < viewportSize.height) {
            if (newWidth * viewportSize.height >= viewportSize.width * newHeight) {
                // aspect ratio greater than screen => wider //
                newWidth = viewportSize.height * newWidth / newHeight;
                newHeight = viewportSize.height;
            } else {
                // aspect ratio smaller than screen => higher //
                newHeight = viewportSize.width * newHeight / newWidth;
                newWidth = viewportSize.width;
            }
            newX = newWidth / 2;
            newY = newHeight / 2;
        } else {
            if (newWidth < viewportSize.width) {
                newX = newWidth / 2;
            } else {
                newX = checkPosition(newX, newWidth, viewportSize.width);
            }
            if (newHeight < viewportSize.height) {
                newY = newHeight / 2;
            } else {
                newY = checkPosition(newY, newHeight, viewportSize.height);
            }
        }
        return new SimpleZoom(new Dimension(newWidth, newHeight), new Point(newX, newY));
    }

    @Override
    public Zoom zoom(Zoom source, double factor, Dimension viewportSize) {
        int newX = round(source.getViewPosition().x * factor);
        int newY = round(source.getViewPosition().y * factor);
        int newWidth = round(source.getGraphSize().width * factor);
        int newHeight = round(source.getGraphSize().height * factor);
        return zoom(newX, newY, newWidth, newHeight, viewportSize);

    }

    @Override
    public Zoom zoomToRectangle(Zoom source, Rectangle target, Dimension viewportSize) {
        double hFactor = viewportSize.width / target.width;
        double vFactor = viewportSize.height / target.height;
        int newWidth = round(source.getGraphSize().width * hFactor);
        int newHeight = round(source.getGraphSize().height * vFactor);
        int newX = round((target.x + target.width / 2) * hFactor);
        int newY = round((target.y + target.height / 2) * vFactor);
        return zoom(newX, newY, newWidth, newHeight, viewportSize);
    }

    public static ZoomBehaviour getInstance() {
        return INSTANCE;
    }
}
