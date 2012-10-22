package org.sybila.parasim.extension.projectManager.model;

import java.util.Set;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface NamedOrthogonalSpace {

    public Pair<Float, Float> getValues(String variable);

    public float getMinValue(String variable);

    public float getMaxValue(String variable);

    public Set<String> getVariables();
}
