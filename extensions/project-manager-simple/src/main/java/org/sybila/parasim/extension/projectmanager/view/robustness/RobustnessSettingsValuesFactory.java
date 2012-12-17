/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.extension.projectmanager.view.robustness;

import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.extension.projectmanager.model.NamedOrthogonalSpace;
import org.sybila.parasim.extension.projectmanager.model.OdeInsideFactory;
import org.sybila.parasim.extension.projectmanager.model.OdeSystemNames;
import org.sybila.parasim.extension.projectmanager.model.OrthogonalSpaceFactory;
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

    public RobustnessSettingsValuesFactory(OdeSystem odeSystem) {
        super(odeSystem);
        spaceFactory = new OrthogonalSpaceFactory(odeSystem);
    }

    private static boolean isDifferent(float a, float b) {
        return (a - b) > 1E-5;
    }

    private boolean isDifferent(Pair<Float, Float> bounds, float defaultValue) {
        if (isDifferent(bounds.first(), bounds.second())) {
            return true;
        }
        return isDifferent(defaultValue, bounds.first());
    }

    public RobustnessSettingsValues get(OrthogonalSpace initialSpace) {
        NamedOrthogonalSpace space = spaceFactory.get(initialSpace);

        Map<String, Pair<Float, Float>> spaceValues = new HashMap<>();

        for (String name : space.getVariables()) {
            Pair<Float, Float> bounds = space.getValues(name);
            Float defaultValue = new OdeSystemNames(getOdeSystem()).getValue(name);
            if (defaultValue == null || isDifferent(bounds, defaultValue)) {
                spaceValues.put(name, bounds);
            }
        }

        return new RobustnessSettingsValues(new SimpleNamedOrthogonalSpace(spaceValues));
    }

    public OrthogonalSpace get(RobustnessSettingsValues values) {
        return spaceFactory.get(values.getInitialSpace());
    }
}
