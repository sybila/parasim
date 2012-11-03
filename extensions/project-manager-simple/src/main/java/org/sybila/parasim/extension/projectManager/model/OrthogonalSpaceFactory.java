package org.sybila.parasim.extension.projectManager.model;

import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.space.OrthogonalSpaceImpl;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OrthogonalSpaceFactory extends OdeInsideFactory {

    public OrthogonalSpaceFactory(OdeSystem odeSystem) {
        super(odeSystem);
    }

    public OrthogonalSpace get(NamedOrthogonalSpace space) {
        return get(space, 0, 0);
    }

    public OrthogonalSpace get(NamedOrthogonalSpace space, int startTime, int endTime) {
        OdeSystem result = OdeUtils.release(getOdeSystem(), space.getVariables());
        OdeSystemNames names = new OdeSystemNames(result);

        float[] min = new float[result.dimension()];
        float[] max = new float[result.dimension()];

        for (int i = 0; i < result.dimension(); i++) {
            String name = names.getName(i);
            Pair<Float, Float> value = space.getValues(name);
            if (value != null) {
                min[i] = value.first();
                max[i] = value.second();
            } else {
                Float variableValue = names.getValue(name);
                if (variableValue != null) {
                    min[i] = variableValue;
                    max[i] = variableValue;
                } else {
                    throw new IllegalArgumentException("Variable `" + name + "' has neither default nor assigned value.");
                }
            }
        }

        return new OrthogonalSpaceImpl(new ArrayPoint(startTime, min), new ArrayPoint(endTime, max), result);
    }

    public NamedOrthogonalSpace get(OrthogonalSpace space) {
        OdeSystem system = space.getOdeSystem();
        OdeSystemNames names = new OdeSystemNames(system);
        Map<String, Pair<Float, Float>> values = new HashMap<>();

        for (int i = 0; i < system.dimension(); i++) {
            values.put(names.getName(i), new Pair<>(space.getMinBounds().getValue(i), space.getMaxBounds().getValue(i)));
        }

        return new SimpleNamedOrthogonalSpace(values);
    }
}
