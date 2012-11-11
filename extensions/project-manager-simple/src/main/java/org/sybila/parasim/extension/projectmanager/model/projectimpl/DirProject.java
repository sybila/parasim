package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import java.util.Map;
import org.sybila.parasim.application.model.LoadedExperiment;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectmanager.model.project.ExperimentResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.FormulaResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.model.project.ResourceList;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNamesResource;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class DirProject implements Project {

    private class ConnectedExperiment extends ExperimentNames {

        public ConnectedExperiment(ExperimentNames target) {
            super(target);
        }

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

    private class ExperimentList implements ResourceList<ExperimentNames> {

        private FileManager files;
        private Map<String, ExperimentNamesResource> resources;

        public void save() throws ResourceException {
            for (ExperimentNamesResource resource : resources.values()) {
                resource.store();
            }
        }

        @Override
        public boolean add(String name, ExperimentNames target) {
            if (resources.containsKey(name)) {
                return false;
            }
            File file = files.createFile(name);
            if (file == null) {
                return false;
            }

            ExperimentNamesResource resource = new ExperimentNamesResource(file);
            resource.setRoot(new ConnectedExperiment(target));
            resources.put(name, resource);

            if (target.getFormulaName() != null) {
                formulae.addExperiment(target.getFormulaName());
            }
            if (target.getInitialSamplingName() != null) {
                samplingList.addExperiment(target.getInitialSamplingName());
            }
            if (target.getInitialSpaceName() != null) {
                initialSpaceList.addExperiment(target.getInitialSpaceName());
            }
            if (target.getPrecisionConfigurationName() != null) {
                precisionList.addExperiment(target.getPrecisionConfigurationName());
            }
            if (target.getSimulationSpaceName() != null) {
                simulationSpaceList.addExperiment(target.getSimulationSpaceName());
            }
            saved = false;
            return true;
        }

        @Override
        public ExperimentNames get(String name) {
            return resources.get(name).getRoot();
        }

        @Override
        public void remove(String name) {
            ExperimentNamesResource resource = resources.get(name);
            if (resource != null) {
                files.deleteFile(name);
                ExperimentNames target = resource.getRoot();
                if (target.getFormulaName() != null) {
                    formulae.removeExperiment(target.getFormulaName());
                }
                if (target.getInitialSamplingName() != null) {
                    samplingList.removeExperiment(target.getInitialSamplingName());
                }
                if (target.getInitialSpaceName() != null) {
                    initialSpaceList.removeExperiment(target.getInitialSpaceName());
                }
                if (target.getPrecisionConfigurationName() != null) {
                    precisionList.removeExperiment(target.getPrecisionConfigurationName());
                }
                if (target.getSimulationSpaceName() != null) {
                    simulationSpaceList.removeExperiment(target.getSimulationSpaceName());
                }
            }
        }

        @Override
        public boolean rename(String name, String newName) {
            if (!resources.containsKey(name)) {
                return false;
            }
            if (resources.containsKey(newName)) {
                return false;
            }
            File target = files.createFile(newName);
            if (target == null) {
                return false;
            }

            ExperimentNamesResource resource = new ExperimentNamesResource(target);
            ExperimentNamesResource src = resources.remove(name);
            resource.setRoot(src.getRoot());
            resources.put(newName, resource);
            saved = false;
            return true;
        }
    }
    //
    private boolean saved = true;
    private OdeSystem odeSystem;
    private DirFormulaeList formulae;
    private XMLResourceList<InitialSampling> samplingList;
    private XMLResourceList<PrecisionConfiguration> precisionList;
    private XMLResourceList<OrthogonalSpace> initialSpaceList;
    private XMLResourceList<OrthogonalSpace> simulationSpaceList;
    private ExperimentList experiments;

    @Override
    public LoadedExperiment getExperiment(ExperimentNames experiment) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResourceList<ExperimentNames> getExperiments() {
        return experiments;
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
        return saved;
    }

    @Override
    public void save() throws ResourceException {
        initialSpaceList.save();
        simulationSpaceList.save();
        samplingList.save();
        precisionList.save();
        experiments.save();
        saved = true;
    }
}
