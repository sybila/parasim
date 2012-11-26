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
