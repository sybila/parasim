package org.sybila.parasim.extension.projectManager.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SimpleNamedInitialSampling implements NamedInitialSampling {

    private final Map<String, Integer> values;

    public SimpleNamedInitialSampling(Map<String, Integer> values) {
        this.values = new HashMap<>(values);
    }

    @Override
    public int getSamples(String variable) {
        Integer result = values.get(variable);
        if (result == null) {
            return 0;
        }
        return result;
    }

    @Override
    public Set<String> getVariables() {
        return Collections.unmodifiableSet(values.keySet());
    }
}
