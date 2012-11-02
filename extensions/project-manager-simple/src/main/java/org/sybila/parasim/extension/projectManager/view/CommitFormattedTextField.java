package org.sybila.parasim.extension.projectManager.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFormattedTextField;
import org.sybila.parasim.util.SimpleLock;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class CommitFormattedTextField extends JFormattedTextField {

    private List<ActionListener> commitListeners = new ArrayList<>();
    private SimpleLock lock = new SimpleLock();

    private void init() {
        addPropertyChangeListener("value", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (lock.isAccessible()) {
                    for (ActionListener listener : commitListeners) {
                        listener.actionPerformed(new ActionEvent(CommitFormattedTextField.this, ActionEvent.ACTION_PERFORMED, "commit"));
                    }
                }
            }
        });
    }

    public CommitFormattedTextField() {
        init();
    }

    public CommitFormattedTextField(AbstractFormatterFactory factory) {
        super(factory);
        init();
    }

    public CommitFormattedTextField(AbstractFormatter formatter) {
        super(formatter);
        init();
    }

    public CommitFormattedTextField(Format format) {
        super(format);
        init();
    }

    public CommitFormattedTextField(Object value) {
        super(value);
        init();
    }

    public CommitFormattedTextField(AbstractFormatterFactory factory, Object currentValue) {
        super(factory, currentValue);
        init();
    }

    public void addCommitListener(ActionListener al) {
        commitListeners.add(al);
    }

    public void removeCommitListener(ActionListener al) {
        commitListeners.remove(al);
    }

    public ActionListener[] getCommitListeners() {
        return commitListeners.toArray(new ActionListener[0]);
    }

    @Override
    public void setValue(Object value) {
        if (lock != null) {
            lock.lock();
        }
        super.setValue(value);
        if (lock != null) {
            lock.unlock();
        }
    }
}
