/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.extension.projectmanager.model;

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

    public OrthogonalSpace get(NamedOrthogonalSpace space, float startTime, float endTime) {
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
