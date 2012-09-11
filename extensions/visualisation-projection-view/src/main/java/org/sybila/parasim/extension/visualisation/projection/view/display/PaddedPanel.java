package org.sybila.parasim.extension.visualisation.projection.view.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PaddedPanel extends JPanel {

    private JComponent view;
    private EmptyBorder padding;

    public PaddedPanel(JComponent view) {
        this.view = view;
        setLayout(new BorderLayout());
        add(view, BorderLayout.CENTER);
        setPadding(0, 0);
        setBackground(view.getBackground());
    }

    private void setPadding(int horizontal, int vertical) {
        padding = new EmptyBorder(vertical, horizontal, vertical, horizontal);
        setBorder(padding);
    }

    private void updateLayout() {
        revalidate();
        setSize(getPreferredSize());
    }

    public void setCentralSize(Dimension size) {
        view.setPreferredSize(size);
        updateLayout();
    }

    public void setHPadding(int extent) {
        setPadding(extent, padding.getBorderInsets().top);
        updateLayout();
    }

    public void setVPadding(int extent) {
        setPadding(padding.getBorderInsets().left, extent);
        updateLayout();
    }
}
