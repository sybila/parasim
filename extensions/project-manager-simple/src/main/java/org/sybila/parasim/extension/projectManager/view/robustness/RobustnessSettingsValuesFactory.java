package org.sybila.parasim.extension.projectManager.view.robustness;

import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.extension.projectManager.model.InitialSamplingFactory;
import org.sybila.parasim.extension.projectManager.model.NamedInitialSampling;
import org.sybila.parasim.extension.projectManager.model.NamedOrthogonalSpace;
import org.sybila.parasim.extension.projectManager.model.OdeInsideFactory;
import org.sybila.parasim.extension.projectManager.model.OdeSystemNames;
import org.sybila.parasim.extension.projectManager.model.OrthogonalSpaceFactory;
import org.sybila.parasim.extension.projectManager.model.SimpleNamedInitialSampling;
import org.sybila.parasim.extension.projectManager.model.SimpleNamedOrthogonalSpace;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class RobustnessSettingsValuesFactory extends OdeInsideFactory {

    private final OrthogonalSpaceFactory spaceFactory;
    private final InitialSamplingFactory samplingFactory;

    public RobustnessSettingsValuesFactory(OdeSystem odeSystem) {
        super(odeSystem);
        spaceFactory = new OrthogonalSpaceFactory(odeSystem);
        samplingFactory = new InitialSamplingFactory(odeSystem);
    }

    public RobustnessSettingsValues get(InitialSampling initialSampling, OrthogonalSpace initialSpace) {
        NamedInitialSampling sampling = samplingFactory.get(initialSampling);
        NamedOrthogonalSpace space = spaceFactory.get(initialSpace);

        Map<String, Integer> sampleValues = new HashMap<>();
        Map<String, Pair<Float, Float>> spaceValues = new HashMap<>();

        for (String name : sampling.getVariables()) {
            int sampleNum = sampling.getSamples(name);
            Pair<Float, Float> bounds = space.getValues(name);
            Float defalutValue = new OdeSystemNames(getOdeSystem()).getValue(name);
            if ((sampleNum != 1) || (defalutValue == null) || (bounds.first() != defalutValue)) {
                sampleValues.put(name, sampleNum);
                spaceValues.put(name, bounds);
            }
        }

        return new RobustnessSettingsValues(new SimpleNamedInitialSampling(sampleValues), new SimpleNamedOrthogonalSpace(spaceValues));
    }

    public Pair<InitialSampling, OrthogonalSpace> get(RobustnessSettingsValues values) {
        return new Pair<>(samplingFactory.get(values.getInitialSampling()), spaceFactory.get(values.getInitialSpace()));
    }
}
