package org.sybila.parasim.extension.projectmanager.model;

import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.computation.density.api.ArrayInitialSampling;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.model.ode.OdeSystem;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class InitialSamplingFactory extends OdeInsideFactory {

    public InitialSamplingFactory(OdeSystem odeSystem) {
        super(odeSystem);
    }

    public InitialSampling get(NamedInitialSampling sampling) {
        OdeSystem result = OdeUtils.release(getOdeSystem(), sampling.getVariables());
        OdeSystemNames names = new OdeSystemNames(result);

        int[] values = new int[result.dimension()];

        for (int i = 0; i < result.dimension(); i++) {
            Integer value = sampling.getSamples(names.getName(i));
            values[i] = (value != null) ? value : 1;
        }

        return new ArrayInitialSampling(result, values);
    }

    public NamedInitialSampling get(InitialSampling sampling) {
        OdeSystem system = sampling.getOdeSystem();
        OdeSystemNames names = new OdeSystemNames(system);
        Map<String, Integer> values = new HashMap<>();

        for (int i = 0; i < system.dimension(); i++) {
            values.put(names.getName(i), sampling.getNumberOfSamples(i));
        }

        return new SimpleNamedInitialSampling(values);
    }
}
