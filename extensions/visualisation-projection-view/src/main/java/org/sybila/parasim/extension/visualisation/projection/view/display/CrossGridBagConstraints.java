package org.sybila.parasim.extension.visualisation.projection.view.display;

import java.awt.GridBagConstraints;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum CrossGridBagConstraints {

    INSTANCE;

    private static GridBagConstraints getEmpty() {
        return new GridBagConstraints();
    }

    public static GridBagConstraints getNorth() {
        GridBagConstraints result = getEmpty();
        result.gridx = 1;
        result.gridy = 0;
        return result;
    }

    public static GridBagConstraints getSouth() {
        GridBagConstraints result = getEmpty();
        result.gridx = 1;
        result.gridy = 2;
        return result;
    }

    public static GridBagConstraints getEast() {
        GridBagConstraints result = getEmpty();
        result.gridx = 2;
        result.gridy = 1;
        return result;
    }

    public static GridBagConstraints getWest() {
        GridBagConstraints result = getEmpty();
        result.gridx = 0;
        result.gridy = 1;
        return result;
    }

    public static GridBagConstraints getCenter() {
        GridBagConstraints result = getEmpty();
        result.gridx = 1;
        result.gridy = 1;
        return result;
    }
}
