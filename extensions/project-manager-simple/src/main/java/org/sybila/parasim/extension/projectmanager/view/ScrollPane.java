package org.sybila.parasim.extension.projectmanager.view;

import java.awt.Component;
import javax.swing.JScrollPane;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ScrollPane extends JScrollPane {

    public ScrollPane(Component view) {
        super(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setBorder(null);
    }
}
