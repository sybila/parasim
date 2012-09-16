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
        public int get(Dimension d) {
            return d.width;
        }

        @Override
        public int get(Point p) {
            return p.x;
        }
    }, Y("height", "y") {

        @Override
        public int get(Dimension d) {
            return d.height;
        }

        @Override
        public int get(Point p) {
            return p.x;
        }
    };
    private String dimString, pointString;

    private DimensionFunctional(String dimensionString, String pointString) {
        this.dimString = dimensionString;
        this.pointString = pointString;
    }

    public abstract int get(Dimension d);

    public abstract int get(Point p);

    public String getDimensionString() {
        return dimString;
    }

    public String getPointString() {
        return pointString;
    }
}
