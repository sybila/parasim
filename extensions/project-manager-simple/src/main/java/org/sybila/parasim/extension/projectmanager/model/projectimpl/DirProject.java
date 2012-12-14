/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.application.model.LoadedExperiment;
import org.sybila.parasim.computation.density.api.ArrayInitialSampling;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectmanager.model.OdeUtils;
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
import org.sybila.parasim.model.verification.stl.Formula;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class DirProject implements Project {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirProject.class);

    private class ConnectedExperiment extends ExperimentNames {

        private final ExperimentNames content;

        public ConnectedExperiment(ExperimentNames target) {
            content = target;
        }

        @Override
        public void setModelName(String modelName) {
            throw new UnsupportedOperationException("Experiments cannot be changed to different model.");
        }

        @Override
        public void setTimeoutAmount(long timeout) {
            if (timeout != getTimeoutAmount()) {
                content.setTimeoutAmount(timeout);
                saved = false;
            }
        }

        @Override
        public void setIterationLimit(int iterationLimit) {
            if (iterationLimit != getIterationLimit()) {
                content.setIterationLimit(iterationLimit);
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
                content.setInitialSpaceName(initialSpaceName);
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
                content.setPrecisionConfigurationName(precisionConfigurationName);
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
                content.setSimulationSpaceName(simulationSpaceName);
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
                content.setFormulaName(formulaName);
                saved = false;
            }
        }

        @Override
        public void setVerificationResultName(String verificationResultName) {
            content.setVerificationResultName(verificationResultName);
            saved = false;
        }

        @Override
        public void setAnnotation(String annotation) {
            content.setAnnotation(annotation);
            saved = false;
        }

        @Override
        public void setTimeoutUnit(TimeUnit timeUnit) {
            content.setTimeoutUnit(timeUnit);
            saved = false;
        }

        @Override
        public String getAnnotation() {
            return content.getAnnotation();
        }

        @Override
        public String getFormulaName() {
            return content.getFormulaName();
        }

        @Override
        public String getInitialSpaceName() {
            return content.getInitialSpaceName();
        }

        @Override
        public int getIterationLimit() {
            return content.getIterationLimit();
        }

        @Override
        public String getModelName() {
            return content.getModelName();
        }

        @Override
        public String getPrecisionConfigurationName() {
            return content.getPrecisionConfigurationName();
        }

        @Override
        public String getSimulationSpaceName() {
            return content.getSimulationSpaceName();
        }

        @Override
        public long getTimeoutAmount() {
            return content.getTimeoutAmount();
        }

        @Override
        public TimeUnit getTimeoutUnit() {
            return content.getTimeoutUnit();
        }

        @Override
        public String getVerificationResultName() {
            return content.getVerificationResultName();
        }

        @Override
        public boolean isFilled() {
            return content.isFilled();
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

        private void checkExperimentNames(ExperimentNames names) {
            String formula = names.getFormulaName();
            if (formula != null && formulae.get(formula) == null) {
                names.setFormulaName(null);
            }

            String initSpace = names.getInitialSpaceName();
            if (initSpace != null && initialSpaceList.get(initSpace) == null) {
                names.setInitialSpaceName(null);
            }

            String simSpace = names.getSimulationSpaceName();
            if (simSpace != null && simulationSpaceList.get(simSpace) == null) {
                names.setSimulationSpaceName(null);
            }

            String precision = names.getPrecisionConfigurationName();
            if (precision != null && precisionList.get(precision) == null) {
                names.setPrecisionConfigurationName(null);
            }
        }

        public void loadResources() {
            resources.clear();
            for (String name : files.getFiles()) {
                if (!resources.containsKey(name)) {
                    ExperimentNamesResource resource = new ExperimentNamesResource(files.getFile(name));
                    try {
                        resource.load();
                        if (resource.getRoot().getModelName().equals(odeName)) {
                            resources.put(name, resource);
                            checkExperimentNames(resource.getRoot());
                            addExperiments(resource.getRoot());
                        } else {
                            LOGGER.warn("Experiment file `" + files.getFile(name) + "' contains different model name.");
                        }
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
        public Set<String> getNames() {
            return Collections.unmodifiableSet(resources.keySet());
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

            target.setModelName(odeName);
            ExperimentNamesResource resource = new ExperimentNamesResource(file);
            resource.setRoot(target);
            try {
                resource.store();
            } catch (ResourceException re) {
                LOGGER.warn("Unable to store new experiment.", re);
                files.deleteFile(name);
                return false;
            }
            resources.put(name, resource);
            addExperiments(target);
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
            ExperimentNamesResource src = resources.get(name);
            resource.setRoot(src.getRoot());
            try {
                resource.store();
            } catch (ResourceException re) {
                LOGGER.warn("Unable to store experiment to new file.", re);
                files.deleteFile(newName);
                return false;
            }
            resources.remove(name);
            resources.put(newName, resource);
            files.deleteFile(name);
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
    private final String odeName;
    private final FileManager results;
    private final DirFormulaeList formulae;
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
        odeName = models[0];
        try {
            odeSystem = SBMLOdeSystemFactory.fromFile(modelManager.getFile(odeName));
        } catch (IOException ioe) {
            throw new ResourceException("Unable to load ODE system from `" + modelManager.getFile(odeName) + "'.", ioe);
        }

        results = new FileManager(directory, ExperimentSuffixes.VERIFICATION_RESULT);
        formulae = new DirFormulaeList(this);
        precisionList = new PrecisionConfigurationResourceList(this);
        initialSpaceList = new InitialSpaceResourceList(this);
        simulationSpaceList = new SimulationSpaceResourceList(this);
        experiments = new ExperimentList();
    }

    public void loadResources() {
        formulae.loadResources();
        precisionList.loadResources();
        initialSpaceList.loadResources();
        simulationSpaceList.loadResources();
        experiments.loadResources();
    }

    public File getProjectDirectory() {
        return dir;
    }

    @Override
    public String getProjectName() {
        return dir.getName();
    }

    @Override
    public Experiment getExperiment(ExperimentNames experiment) {
        if (!experiment.isFilled()) {
            return null;
        }
        if (!experiment.getModelName().equals(odeName)) {
            return null;
        }

        if (!experiment.isFilled()) {
            return null;
        }

        OrthogonalSpace initSpace = initialSpaceList.get(experiment.getInitialSpaceName());
        OdeSystem system = initSpace.getOdeSystem();
        OrthogonalSpace simSpace = OdeUtils.reSystemSpace(simulationSpaceList.get(experiment.getSimulationSpaceName()), system);
        PrecisionConfiguration precision = precisionList.get(experiment.getPrecisionConfigurationName());
        Formula formula = formulae.get(experiment.getFormulaName());

        InitialSampling sampling = new ArrayInitialSampling(odeSystem, 1);

        String resultName = experiment.getVerificationResultName();
        if (resultName != null) {
            return new LoadedExperiment(system, formula, initSpace, simSpace, precision, sampling, experiment.getTimeoutAmount(), experiment.getTimeoutUnit(), experiment.getIterationLimit(), results.getFile(resultName));
        } else {
            return new LoadedExperiment(system, formula, initSpace, simSpace, precision, sampling, experiment.getTimeoutAmount(), experiment.getTimeoutUnit(), experiment.getIterationLimit());
        }
    }

    @Override
    public boolean hasResult(ExperimentNames experiment) {
        if (experiment.getVerificationResultName() == null) {
            return false;
        }
        File result = results.getFile(experiment.getVerificationResultName());
        return (result.isFile());
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
        if (!precisionList.isSaved()) {
            return false;
        }
        return saved;
    }

    @Override
    public void save() throws ResourceException {
        initialSpaceList.save();
        simulationSpaceList.save();
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
