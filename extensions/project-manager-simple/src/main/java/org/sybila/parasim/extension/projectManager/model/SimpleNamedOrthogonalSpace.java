package org.sybila.parasim.extension.projectManager.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class SimpleNamedOrthogonalSpace implements NamedOrthogonalSpace {

    private final Map<String, Pair<Float, Float>> values;

    public SimpleNamedOrthogonalSpace(Map<String, Pair<Float, Float>> values) {
        this.values = new HashMap<>(values);
    }

    @Override
    public Pair<Float, Float> getValues(String variable) {
        return values.get(variable);
    }

    @Override
    public float getMaxValue(String variable) {
        Pair<Float, Float> res = values.get(variable);
        if (res == null) {
            return Float.NaN;
        }
        return res.second();
    }

    @Override
    public float getMinValue(String variable) {
        Pair<Float, Float> res = values.get(variable);
        if (res == null) {
            return Float.NaN;
        }
        return res.first();
    }

    @Override
    public Set<String> getVariables() {
        return Collections.unmodifiableSet(values.keySet());
    }
}
