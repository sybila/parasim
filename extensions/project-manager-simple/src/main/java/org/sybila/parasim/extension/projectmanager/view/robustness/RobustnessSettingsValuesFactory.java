/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.extension.projectmanager.view.robustness;

import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.extension.projectmanager.model.InitialSamplingFactory;
import org.sybila.parasim.extension.projectmanager.model.NamedInitialSampling;
import org.sybila.parasim.extension.projectmanager.model.NamedOrthogonalSpace;
import org.sybila.parasim.extension.projectmanager.model.OdeInsideFactory;
import org.sybila.parasim.extension.projectmanager.model.OdeSystemNames;
import org.sybila.parasim.extension.projectmanager.model.OrthogonalSpaceFactory;
import org.sybila.parasim.extension.projectmanager.model.SimpleNamedInitialSampling;
import org.sybila.parasim.extension.projectmanager.model.SimpleNamedOrthogonalSpace;
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

    private static boolean isDifferent(float a, float b) {
        return (a - b) > 1E-5;
    }

    private boolean isDifferent(int sampleNum, Pair<Float, Float> bounds, float defaultValue) {
        if (sampleNum != 1) {
            return true;
        }
        if (isDifferent(bounds.first(), bounds.second())) {
            return true;
        }
        return isDifferent(defaultValue, bounds.first());
    }

    public RobustnessSettingsValues get(InitialSampling initialSampling, OrthogonalSpace initialSpace) {
        NamedInitialSampling sampling = samplingFactory.get(initialSampling);
        NamedOrthogonalSpace space = spaceFactory.get(initialSpace);

        Map<String, Integer> sampleValues = new HashMap<>();
        Map<String, Pair<Float, Float>> spaceValues = new HashMap<>();

        for (String name : sampling.getVariables()) {
            int sampleNum = sampling.getSamples(name);
            Pair<Float, Float> bounds = space.getValues(name);
            Float defaultValue = new OdeSystemNames(getOdeSystem()).getValue(name);
            if (defaultValue == null || isDifferent(sampleNum, bounds, defaultValue)) {
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
