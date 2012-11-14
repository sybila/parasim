package org.sybila.parasim.extension.projectmanager.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

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

        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "openDir");
        getActionMap().put("openDir", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (getSelectedFile() != null && getSelectedFile().isDirectory()) {
                    setCurrentDirectory(getSelectedFile());
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
