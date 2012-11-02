package org.sybila.parasim.extension.projectManager.project;

import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectManager.names.ExperimentNames;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.xml.XMLRepresentable;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
 */
public interface Project extends XMLRepresentable {

    public OdeSystem getOdeSystem();

    public String getModelName();

    public ResourceList<Formula> getFormulae();

    public ResourceList<OrthogonalSpace> getInitialSpaces();

    public ResourceList<OrthogonalSpace> getSimulationSpaces();

    public ResourceList<PrecisionConfiguration> getPrecisionConfigurations();

    public ResourceList<InitialSampling> getInitialSamplings();

    public ResourceList<VerificationResult> getVerificationResults();

    public ResourceList<ExperimentNames> getExperimentList();

    public Experiment getExperiment(ExperimentNames names);

    public String getActiveExperiment();
}
