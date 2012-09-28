package org.sybila.parasim.extension.visualisation.projection.view.display.util;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum Orientation {

    HORIZONTAL(DimensionFunctional.X), VERTICAL(DimensionFunctional.Y);
    private Orientation other;
    private DimensionFunctional dimFunc;

    static {
        HORIZONTAL.other = VERTICAL;
        VERTICAL.other = HORIZONTAL;
    }

    private Orientation(DimensionFunctional functional) {
        this.dimFunc = functional;
    }

    public Orientation other() {
        return other;
    }

    public DimensionFunctional getDimensionFunctional() {
        return dimFunc;
    }
}
