package org.sybila.parasim.extension.projectmanager.model.project;

import org.sybila.parasim.application.model.LoadedExperiment;
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

    public OdeSystem getOdeSystem();

    public String getProjectName();

    public FormulaResourceList getFormulae();

    public ExperimentResourceList<OrthogonalSpace> getSimulationSpaces();

    public ExperimentResourceList<PrecisionConfiguration> getPrecisionsConfigurations();

    public ExperimentResourceList<OrthogonalSpace> getInitialSpaces();

    public ExperimentResourceList<InitialSampling> getInitialSamplings();

    public ResourceList<ExperimentNames> getExperiments();

    public boolean isSaved();

    public void save() throws ResourceException;

    public LoadedExperiment getExperiment(ExperimentNames experiment);
}
