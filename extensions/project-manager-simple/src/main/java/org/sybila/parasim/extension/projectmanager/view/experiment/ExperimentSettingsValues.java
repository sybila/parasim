package org.sybila.parasim.extension.projectmanager.view.experiment;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class ExperimentSettingsValues {

    private final int iterationLimit;
    private final long timeout;

    public ExperimentSettingsValues(int iterationLimit, long timeout) {
        this.iterationLimit = iterationLimit;
        this.timeout = timeout;
    }

    public int getIterationLimit() {
        return iterationLimit;
    }

    public long getTimeout() {
        return timeout;
    }
}
