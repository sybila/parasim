package org.sybila.parasim.extension.projectmanager.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ListeningFileChooser extends JFileChooser {

    private final List<ActionListener> changeListeners = new ArrayList<>();

    public ListeningFileChooser() {
        super(System.getProperty("user.home"));
        setControlButtonsAreShown(false);
        setMultiSelectionEnabled(false);
        addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                ActionEvent event = new ActionEvent(ListeningFileChooser.this, ActionEvent.ACTION_PERFORMED, JFileChooser.SELECTED_FILE_CHANGED_PROPERTY);
                for (ActionListener change : changeListeners) {
                    change.actionPerformed(event);
                }
            }
        });
    }

    public void addSelectedFileChangedListener(ActionListener al) {
        changeListeners.add(al);
    }

    public void removeSelectedFileChangedListener(ActionListener al) {
        changeListeners.remove(al);
    }

    public ActionListener[] getSelectedFileChangedListeners() {
        return changeListeners.toArray(new ActionListener[0]);
    }
}
