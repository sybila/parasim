package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import org.sybila.parasim.application.model.LoadedExperiment;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectmanager.model.project.ExperimentResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.FormulaResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.model.project.ResourceList;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class DirProject implements Project {

    private class ConnectedExperiment extends ExperimentNames {

        @Override
        public void setModelName(String modelName) {
            throw new UnsupportedOperationException("Experiments cannot be changed to different model.");
        }

        @Override
        public void setTimeout(long timeout) {
            if (timeout != getTimeout()) {
                super.setTimeout(timeout);
                saved = false;
            }
        }

        @Override
        public void setIterationLimit(int iterationLimit) {
            if (iterationLimit != iterationLimit) {
                super.setIterationLimit(iterationLimit);
                saved = false;
            }
        }

        @Override
        public void setInitialSamplingName(String initialSamplingName) {
            if (!initialSamplingName.equals(getInitialSamplingName())) {
                if (getInitialSamplingName() != null) {
                    samplingList.removeExperiment(getInitialSamplingName());
                }
                samplingList.addExperiment(initialSamplingName);
                super.setInitialSamplingName(initialSamplingName);
                saved = false;
            }
        }

        @Override
        public void setInitialSpaceName(String initialSpaceName) {
            if (!initialSpaceName.equals(getInitialSpaceName())) {
                if (getInitialSpaceName() != null) {
                    initialSpaceList.removeExperiment(getInitialSpaceName());
                }
                initialSpaceList.addExperiment(initialSpaceName);
                super.setInitialSpaceName(initialSpaceName);
                saved = false;
            }
        }

        @Override
        public void setPrecisionConfigurationName(String precisionConfigurationName) {
            if (!precisionConfigurationName.equals(getPrecisionConfigurationName())) {
                if (getPrecisionConfigurationName() != null) {
                    precisionList.removeExperiment(getPrecisionConfigurationName());
                }
                precisionList.addExperiment(precisionConfigurationName);
                super.setPrecisionConfigurationName(precisionConfigurationName);
                saved = false;
            }
        }

        @Override
        public void setSimulationSpaceName(String simulationSpaceName) {
            if (!simulationSpaceName.equals(getSimulationSpaceName())) {
                if (getSimulationSpaceName() != null) {
                    simulationSpaceList.removeExperiment(getSimulationSpaceName());
                }
                simulationSpaceList.addExperiment(simulationSpaceName);
                super.setSimulationSpaceName(simulationSpaceName);
                saved = false;
            }
        }

        @Override
        public void setFormulaName(String formulaName) {
            if (!formulaName.equals(getFormulaName())) {
                if (getFormulaName() != null) {
                    formulae.removeExperiment(getFormulaName());
                }
                formulae.addExperiment(formulaName);
                super.setFormulaName(formulaName);
                saved = false;
            }
        }
    }
    //
    private boolean saved = true;
    private File projectDir;
    private OdeSystem odeSystem;
    private DirFormulaeList formulae;
    private XMLResourceList<InitialSampling> samplingList;
    private XMLResourceList<PrecisionConfiguration> precisionList;
    private XMLResourceList<OrthogonalSpace> initialSpaceList;
    private XMLResourceList<OrthogonalSpace> simulationSpaceList;

    @Override
    public LoadedExperiment getExperiment(ExperimentNames experiment) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResourceList<ExperimentNames> getExperiments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FormulaResourceList getFormulae() {
        return formulae;
    }

    @Override
    public ExperimentResourceList<InitialSampling> getInitialSamplings() {
        return samplingList;
    }

    @Override
    public ExperimentResourceList<OrthogonalSpace> getInitialSpaces() {
        return initialSpaceList;
    }

    @Override
    public OdeSystem getOdeSystem() {
        return odeSystem;
    }

    @Override
    public ExperimentResourceList<PrecisionConfiguration> getPrecisionsConfigurations() {
        return precisionList;
    }

    @Override
    public ExperimentResourceList<OrthogonalSpace> getSimulationSpaces() {
        return simulationSpaceList;
    }

    @Override
    public boolean isSaved() {
        if (!initialSpaceList.isSaved()) {
            return false;
        }
        if (!simulationSpaceList.isSaved()) {
            return false;
        }
        if (!samplingList.isSaved()) {
            return false;
        }
        if (!precisionList.isSaved()) {
            return false;
        }

        //TODO

        return saved;
    }

    @Override
    public void save() throws ResourceException {
        initialSpaceList.save();
        simulationSpaceList.save();
        samplingList.save();
        precisionList.save();
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
