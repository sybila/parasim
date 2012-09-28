package org.sybila.parasim.extension.visualisation.projection.view.display.util;

import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum DimensionFunctional {

    X("width", "x") {

        @Override
        public <T> T get(T x, T y) {
            return x;
        }

        @Override
        public Dimension composeDimension(int thisDimensionValue, int otherValue) {
            return new Dimension(thisDimensionValue, otherValue);
        }

        @Override
        public Point composePoint(int thisDimensionValue, int otherValue) {
            return new Point(thisDimensionValue, otherValue);
        }
    }, Y("height", "y") {

        @Override
        public <T> T get(T x, T y) {
            return y;
        }

        @Override
        public Dimension composeDimension(int thisDimensionValue, int otherValue) {
            return new Dimension(otherValue, thisDimensionValue);
        }

        @Override
        public Point composePoint(int thisDimensionValue, int otherValue) {
            return new Point(otherValue, thisDimensionValue);
        }
    };
    private String dimString, pointString;

    private DimensionFunctional(String dimensionString, String pointString) {
        this.dimString = dimensionString;
        this.pointString = pointString;
    }

    public abstract <T> T get(T x, T y);

    //public int get(int x, int y)
    public int get(Dimension d) {
        return get(d.width, d.height);
    }

    public int get(Point p) {
        return get(p.x, p.y);
    }

    public abstract Point composePoint(int thisDimensionValue, int otherValue);

    public abstract Dimension composeDimension(int thisDimensionValue, int otherValue);

    public String getDimensionString() {
        return dimString;
    }

    public String getPointString() {
        return pointString;
    }
}
