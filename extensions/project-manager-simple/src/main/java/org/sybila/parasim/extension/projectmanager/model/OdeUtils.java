package org.sybila.parasim.extension.projectmanager.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.space.OrthogonalSpaceImpl;
import org.sybila.parasim.model.trajectory.ArrayPoint;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OdeUtils {

    private OdeUtils() {
    }

    public static OdeSystem substituteAll(OdeSystem target) {
        List<ParameterValue> params = new ArrayList<>();
        for (int i = 0; i < target.dimension(); i++) {
            if (target.isParamater(i)) {
                ParameterValue value = target.getDeclaredParamaterValue(target.getParameter(i));
                if (value != null) {
                    params.add(value);
                }
            }
        }
        return target.substitute(params);
    }

    public static OdeSystem release(OdeSystem target, Set<String> names) {
        List<Expression> params = new ArrayList<>();
        for (Map.Entry<String, Parameter> param : target.getAvailableParameters().entrySet()) {
            if (param.getValue().isSubstituted() && names.contains(param.getKey())) {
                params.add(param.getValue());
            }
        }
        return target.release(params);
    }

    public static OrthogonalSpace reSystemSpace(OrthogonalSpace target, OdeSystem system) {
        int dim = system.dimension();
        float[] min = Arrays.copyOf(target.getMinBounds().toArray(), dim);
        float[] max = Arrays.copyOf(target.getMaxBounds().toArray(), dim);
        for (int i = target.getDimension(); i < dim; i++) {
            ParameterValue value = system.getDeclaredParamaterValue(system.getParameter(i));
            float bound = (value != null) ? value.getValue() : 0;
            min[i] = bound;
            max[i] = bound;
        }

        return new OrthogonalSpaceImpl(new ArrayPoint(target.getMinBounds().getTime(), min),
                new ArrayPoint(target.getMaxBounds().getTime(), max), system);
    }
}
