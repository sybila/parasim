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
package org.sybila.parasim.extension.projectmanager.model.project;

import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Project {

    OdeSystem getOdeSystem();

    String getProjectName();

    FormulaResourceList getFormulae();

    ExperimentResourceList<OrthogonalSpace> getSimulationSpaces();

    ExperimentResourceList<PrecisionConfiguration> getPrecisionsConfigurations();

    ExperimentResourceList<OrthogonalSpace> getInitialSpaces();

    ExperimentResourceList<InitialSampling> getInitialSamplings();

    ResourceList<ExperimentNames> getExperiments();

    boolean isSaved();

    void save() throws ResourceException;

    Experiment getExperiment(ExperimentNames experiment);

    boolean hasResult(ExperimentNames experiment);
}
