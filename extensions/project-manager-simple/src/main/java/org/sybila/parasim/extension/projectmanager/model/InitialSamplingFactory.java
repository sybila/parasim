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
            int value = sampling.getSamples(names.getName(i));
            values[i] = (value != 0) ? value : 1;
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
