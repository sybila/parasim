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
package org.sybila.parasim.application.model;

import org.sybila.parasim.computation.density.api.InitialSamplingResource;
import org.sybila.parasim.computation.simulation.api.PrecisionConfigurationResource;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpaceResource;
import org.sybila.parasim.model.verification.result.VerificationResultResource;
import org.sybila.parasim.model.verification.stl.FormulaResource;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Experiment {

    OdeSystem getOdeSystem();

    FormulaResource getSTLFormulaResource();

    OrthogonalSpaceResource getInitialSpaceResource();

    OrthogonalSpaceResource getSimulationSpaceResource();

    PrecisionConfigurationResource getPrecisionConfigurationResources();

    InitialSamplingResource getInitialSamplingResource();

    long getTimeoutInMilliSeconds();

    VerificationResultResource getVerificationResultResource();
}
