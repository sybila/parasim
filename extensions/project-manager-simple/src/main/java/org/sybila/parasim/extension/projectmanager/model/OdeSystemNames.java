/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.sybila.parasim.computation.simulation.api.ArrayPrecisionConfiguration;
import org.sybila.parasim.extension.projectmanager.view.robustness.RobustnessSettingsValues;
import org.sybila.parasim.extension.projectmanager.view.simulation.SimulationSettingsValues;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.SubstitutionValue;
import org.sybila.parasim.model.math.VariableValue;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OdeSystemNames {

    private final OdeSystem system;

    public OdeSystemNames(OdeSystem system) {
        if (system == null) {
            throw new IllegalArgumentException("Argument (system) is null.");
        }
        this.system = system;
    }

    public Set<String> getVariables() {
        return system.getVariables().keySet();
    }

    public Set<String> getParameters() {
        return system.getAvailableParameters().keySet();
    }

    public boolean isVariable(String name) {
        return system.getVariables().containsKey(name);
    }

    public boolean isParameter(String name) {
        return system.getAvailableParameters().containsKey(name);
    }

    public int getDimension(String variableName) {
        OdeSystemVariable var = system.getVariables().get(variableName);
        if (var == null) {
            throw new IllegalArgumentException("System contains no variable `" + variableName + "'.");
        }
        return var.getIndex();
    }

    public Float getValue(String name) {
        OdeSystemVariable var = system.getVariables().get(name);
        if (var != null) {
            VariableValue value = system.getInitialVariableValue(var);
            if (value != null) {
                return ((Constant) value.getSubstitution()).getValue();
            } else {
                return null;
            }
        }
        Parameter par = system.getAvailableParameters().get(name);
        if (par != null) {
            SubstitutionValue<Parameter> declared = system.getDeclaredParamaterValue(par);
            if (declared == null) {
                throw new IllegalStateException("There is no declared value for parameter " + par);
            }
            return ((Constant) declared.getSubstitution()).getValue();
        }
        throw new IllegalArgumentException("Ode system contains no variable or parameter of such value.");
    }

    public String getVariable(int dimension) {
        return system.getVariable(dimension).getName();
    }

    public String getName(int dimension) {
        if (dimension < 0 || dimension >= system.dimension()) {
            throw new IndexOutOfBoundsException("Dimension out of bounds: " + dimension);
        }
        if (system.isParamater(dimension)) {
            return system.getParameter(dimension).getName();
        } else if (system.isVariable(dimension)) {
            return system.getVariable(dimension).getName();
        } else {
            throw new IllegalStateException("Dimension that is not out of bounds, and is neither a parameter or a variable.");
        }
    }

    public boolean isSubstituted(String paramName) {
        Parameter param = system.getAvailableParameters().get(paramName);
        if (param == null) {
            throw new IllegalArgumentException("System contains no such parameter.");
        }
        return param.isSubstituted();
    }

    public SimulationSettingsValues getDefaultSimulationSettingsValues() {
        float[] absolute = new float[system.dimension()];
        Arrays.fill(absolute, 0f);

        Map<String, Pair<Float, Float>> space = new HashMap<>();
        for (String name : getVariables()) {
            Float value = getValue(name);
            Pair<Float, Float> bounds;
            if (value == null) {
                value = 0f;
            }
            if (value == 0) {
                bounds = new Pair<>(0f, 100f);
            } else {
                int magnitude = Double.valueOf(Math.ceil(Math.log10(value))).intValue() + 1;
                bounds = new Pair<>(0f, Double.valueOf(Math.pow(10, magnitude)).floatValue());
            }
            space.put(name, bounds);
        }

        return new SimulationSettingsValues(
                new ArrayPrecisionConfiguration(absolute, 0.1f, 0.1f),
                new SimpleNamedOrthogonalSpace(space),
                0, 100);
    }

    public RobustnessSettingsValues getDefaultRobustnessSettingsValues() {
        Map<String, Pair<Float, Float>> space = new HashMap<>();
        Map<String, Integer> sampling = new HashMap<>();

        Set<String> names = new HashSet<>(getVariables());
        names.addAll(getParameters());
        for (String name : names) {
            Float value = getValue(name);
            if (value == null) {
                space.put(name, new Pair<>(0f, 0f));
                sampling.put(name, 1);
            }
        }

        return new RobustnessSettingsValues(new SimpleNamedOrthogonalSpace(space));
    }
}
