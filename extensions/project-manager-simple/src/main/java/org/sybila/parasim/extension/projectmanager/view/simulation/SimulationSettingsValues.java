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
package org.sybila.parasim.extension.projectmanager.view.simulation;

import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectmanager.model.NamedOrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class SimulationSettingsValues {

    private final PrecisionConfiguration precision;
    private final NamedOrthogonalSpace space;
    private final float startTime, endTime;

    public SimulationSettingsValues(PrecisionConfiguration precision, NamedOrthogonalSpace space, float startTime, float endTime) {
        if (precision == null) {
            throw new IllegalArgumentException("Argument (precision) is null.");
        }
        if (space == null) {
            throw new IllegalArgumentException("Argument (space) is null.");
        }
        this.precision = precision;
        this.space = space;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public PrecisionConfiguration getPrecisionConfiguration() {
        return precision;
    }

    public NamedOrthogonalSpace getSimulationSpace() {
        return space;
    }

    public float getSimulationStart() {
        return startTime;
    }

    public float getSimulationEnd() {
        return endTime;
    }
}
