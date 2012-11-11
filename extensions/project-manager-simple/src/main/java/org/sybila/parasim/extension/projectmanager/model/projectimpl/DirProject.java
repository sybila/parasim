package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.application.model.LoadedExperiment;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectmanager.model.project.ExperimentResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.FormulaResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.model.project.ResourceList;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNamesResource;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.sbml.SBMLOdeSystemFactory;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class DirProject implements Project {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirProject.class);

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

    public static interface ExperimentAction {

        public void apply(ExperimentNames target);
    }

    private class ExperimentList implements ResourceList<ExperimentNames> {

        private final FileManager files;
        private final Map<String, ExperimentNamesResource> resources = new HashMap<>();

        public ExperimentList() {
            files = new FileManager(dir, ExperimentSuffixes.EXPERIMENT);
        }

        public void loadResources() {
            resources.clear();
            for (String name : files.getFiles()) {
                if (!resources.containsKey(name)) {
                    ExperimentNamesResource resource = new ExperimentNamesResource(files.getFile(name));
                    try {
                        resource.load();
                        resources.put(name, resource);
                        addExperiments(resource.getRoot());
                    } catch (ResourceException re) {
                        LOGGER.warn("Unable to load `" + files.getFile(name) + "'.", re);
                    }
                }
            }
        }

        public void save() throws ResourceException {
            for (ExperimentNamesResource resource : resources.values()) {
                resource.store();
            }
        }

        private void addExperiments(ExperimentNames target) {
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
            resource.setRoot(target);
            resources.put(name, resource);
            addExperiments(target);
            saved = false;
            return true;
        }

        @Override
        public ExperimentNames get(String name) {
            return new ConnectedExperiment(resources.get(name).getRoot());
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

        public void applyAction(ExperimentAction target) {
            for (ExperimentNamesResource resource : resources.values()) {
                target.apply(resource.getRoot());
            }
        }
    }
    //
    private final File dir;
    private boolean saved = true;
    private final OdeSystem odeSystem;
    private final DirFormulaeList formulae;
    private final XMLResourceList<InitialSampling> samplingList;
    private final XMLResourceList<PrecisionConfiguration> precisionList;
    private final XMLResourceList<OrthogonalSpace> initialSpaceList;
    private final XMLResourceList<OrthogonalSpace> simulationSpaceList;
    private final ExperimentList experiments;

    public DirProject(File directory) throws ResourceException {
        if (directory == null) {
            throw new IllegalArgumentException("Argument (directory) is null.");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Argument (directory) is not a directory.");
        }
        dir = directory;

        FileManager modelManager = new FileManager(dir, ExperimentSuffixes.MODEL);
        String[] models = modelManager.getFiles();
        if (models.length == 0) {
            throw new ResourceException("There is no model file in the directory.");
        }
        if (models.length > 1) {
            throw new ResourceException("There is more than one model in the directory.");
        }
        try {
            odeSystem = SBMLOdeSystemFactory.fromFile(modelManager.getFile(models[0]));
        } catch (IOException ioe) {
            throw new ResourceException("Unable to load ODE system from `" + modelManager.getFile(models[0]) + "'.", ioe);
        }

        formulae = new DirFormulaeList(this);
        samplingList = new InitialSamplingResourceList(this);
        precisionList = new PrecisionConfigurationResourceList(this);
        initialSpaceList = new InitialSpaceResourceList(this);
        simulationSpaceList = new SimulationSpaceResourceList(this);
        experiments = new ExperimentList();
    }

    public void loadResources() {
        formulae.loadResources();
        samplingList.loadResources();
        precisionList.loadResources();
        initialSpaceList.loadResources();
        simulationSpaceList.loadResources();
        experiments.loadResources();
    }

    public File getProjectDirectory() {
        return dir;
    }

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

    void removeFormula(final String name) {
        experiments.applyAction(new ExperimentAction() {

            @Override
            public void apply(ExperimentNames target) {
                if (name.equals(target.getFormulaName())) {
                    target.setFormulaName(null);
                }
            }
        });
    }

    void renameFormula(final String name, final String newName) {
        experiments.applyAction(new ExperimentAction() {

            @Override
            public void apply(ExperimentNames target) {
                if (name.equals(target.getFormulaName())) {
                    target.setFormulaName(newName);
                }
            }
        });
    }

    void applyAction(ExperimentAction target) {
        experiments.applyAction(target);
    }
}