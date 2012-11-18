package org.sybila.parasim.extension.projectmanager.model.components;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ExperimentAvailableListener {

    public void experimentReady();

    public void invalidate();
}
