package org.sybila.parasim.extension.projectManager.view;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class TableConstraints {

    private TableConstraints() {
    }

    private static GridBagConstraints getDefaultConstraints() {
        GridBagConstraints result = new GridBagConstraints();
        result.weightx = 1;
        result.weighty = 1;
        result.fill = GridBagConstraints.BOTH;
        result.insets = new Insets(2, 1, 2, 1);
        return result;
    }

    public static GridBagConstraints getHeaderConstraints(int x) {
        GridBagConstraints result = getDefaultConstraints();
        result.gridx = x;
        result.gridy = 0;
        return result;
    }

    public static GridBagConstraints getRowConstraints(int y) {
        GridBagConstraints result = getDefaultConstraints();
        result.gridx = 0;
        result.gridy = y;
        return result;
    }

    public static GridBagConstraints getCellConstraints(int x, int y) {
        GridBagConstraints result = getDefaultConstraints();
        result.gridx = x;
        result.gridy = y;
        return result;
    }

    public static JLabel getHeaderLabel(String name) {
        JLabel result = new JLabel(name);
        result.setHorizontalAlignment(JLabel.CENTER);
        return result;
    }

    public static JLabel getRowLabel(String name) {
        JLabel result = new JLabel(name);
        result.setHorizontalAlignment(JLabel.RIGHT);
        return result;
    }
}