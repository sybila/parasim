package org.sybila.parasim.extension.projectmanager.model;

import java.util.Set;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface NamedInitialSampling {

    public int getSamples(String variable);

    public Set<String> getVariables();
}
