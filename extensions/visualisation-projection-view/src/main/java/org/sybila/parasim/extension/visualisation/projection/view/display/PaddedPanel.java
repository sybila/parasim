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

    public void setExtraSpace(int horizontal, int vertical) {
        int left = horizontal / 2;
        int right = (horizontal % 2 == 0) ? left : left + 1;
        int top = vertical / 2;
        int bottom = (vertical % 2 == 0) ? top : top + 1;
        padding = new EmptyBorder(top, left, bottom, right);
        setBorder(padding);
        updateLayout();
    }

    private void updateLayout() {
        revalidate();
        setSize(getPreferredSize());
    }

    public void setCentralSize(Dimension size) {
        view.setPreferredSize(size);
        updateLayout();
    }
}
