package org.sybila.parasim.extension.projectmanager.view.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import javax.swing.JLabel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class WarningLabel {

    private final JLabel label;

    public WarningLabel(Container parent) {
        label = new JLabel("This configuration is used in an experiment.");
        label.setIcon(IconSource.getInfoIcon());
        parent.add(label, BorderLayout.PAGE_START);
        label.setForeground(Color.GRAY);
        label.setVisible(false);
    }

    public void setVisible(boolean visibility) {
        label.setVisible(visibility);
    }
}
