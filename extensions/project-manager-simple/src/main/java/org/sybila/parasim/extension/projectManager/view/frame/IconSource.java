package org.sybila.parasim.extension.projectManager.view.frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class IconSource {

    private IconSource() {
    }

    private static Action getAction(String iconSrc, String alternateText, final ActionListener listener) {
        Icon icon = UIManager.getIcon(iconSrc);
        if (icon != null) {
            return new AbstractAction("", icon) {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    listener.actionPerformed(ae);
                }
            };
        }
        return new AbstractAction(alternateText) {

            @Override
            public void actionPerformed(ActionEvent ae) {
                listener.actionPerformed(ae);
            }
        };
    }

    public static Action getNewAction(ActionListener listener) {
        return getAction("FileView.fileIcon", "New project", listener);
    }

    public static Action getLoadAction(ActionListener listener) {
        return getAction("FileView.directoryIcon", "Open project", listener);
    }

    public static Action getSaveAction(ActionListener listener) {
        return getAction("FileView.floppyDriveIcon", "Save project", listener);
    }
}
