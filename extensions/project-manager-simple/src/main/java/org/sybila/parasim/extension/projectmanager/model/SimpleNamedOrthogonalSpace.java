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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class SimpleNamedOrthogonalSpace implements NamedOrthogonalSpace {

    private final Map<String, Pair<Float, Float>> values;

    public SimpleNamedOrthogonalSpace(Map<String, Pair<Float, Float>> values) {
        this.values = new HashMap<>(values);
    }

    @Override
    public Pair<Float, Float> getValues(String variable) {
        return values.get(variable);
    }

    @Override
    public float getMaxValue(String variable) {
        Pair<Float, Float> res = values.get(variable);
        if (res == null) {
            return Float.NaN;
        }
        return res.second();
    }

    @Override
    public float getMinValue(String variable) {
        Pair<Float, Float> res = values.get(variable);
        if (res == null) {
            return Float.NaN;
        }
        return res.first();
    }

    @Override
    public Set<String> getVariables() {
        return Collections.unmodifiableSet(values.keySet());
    }
}
