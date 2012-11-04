package org.sybila.parasim.extension.projectManager.model.project;

import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Project {

    public OdeSystem getOdeSystem();

    public ResourceList<OrthogonalSpace> getSimulationSpaces();

    public ResourceList<PrecisionConfiguration> getPrecisionsConfigurations();

    public ResourceList<OrthogonalSpace> getInitialSpaces();

    public ResourceList<InitialSampling> getInitialSamplings();
}
