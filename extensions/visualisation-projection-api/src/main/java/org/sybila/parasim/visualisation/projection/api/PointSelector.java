package org.sybila.parasim.visualisation.projection.api;

/**
 * Notifies listeners when a point is selected.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface PointSelector {

    /**
     * Adds target listener to the collection of listeners which is notified.
     *
     * @param target Listener which should be notified.
     */
    public void addPointSelectionListener(PointSelectionListener target);

    /**
     * Removes target listener from the collection of listeners which is
     * notified.
     *
     * @param target Listener which should not be notified.
     */
    public void removePointSelectionListener(PointSelectionListener target);

    /**
     * Removes all notified listeneres, so that when a point is selected, none
     * is notified.
     */
    public void clearPointSelectionListeners();
}
