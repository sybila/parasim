package org.sybila.parasim.extension.projectmanager.oldoroject;

import java.io.File;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.extension.projectmanager.names.ProjectNames;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
 */
public enum DirProjectNamesFactory {

    INSTANCE;

    public ProjectNames get(File dir) throws ResourceException {
        if (dir == null) {
            throw new IllegalArgumentException("Argument (dir) is null.");
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Argument (dir) should be a directory.");
        }
        ProjectNames result = new ProjectNames();

        boolean hasModel = false;
        for (File target : dir.listFiles()) {
            ExperimentSuffixes suff = ExperimentSuffixes.getSuffix(target.getName());
            if (suff != null) {
                String name = suff.remove(target.getName());
                switch (suff) {
                    case MODEL:
                        if (hasModel) {
                            throw new ResourceException("There are two model files.");
                        }
                        result.setModelName(name);
                        hasModel = true;
                        break;
                    case EXPERIMENT:
                        result.getExperimentsNames().add(name);
                        break;
                    case FORMULA:
                        result.getFormulaeNames().add(name);
                        break;
                    case INITIAL_SAMPLING:
                        result.getInitialSamplingsNames().add(name);
                        break;
                    case INITIAL_SPACE:
                        result.getInitialSpacesNames().add(name);
                        break;
                    case PRECISION_CONFIGURATION:
                        result.getPrecisionConfigurationsNames().add(name);
                        break;
                    case SIMULATION_SPACE:
                        result.getSimulationSpacesNames().add(name);
                        break;
                }
            }
        }

        if (!hasModel) {
            throw new ResourceException("There is no model file.");
        }
        return result;
    }

    public static DirProjectNamesFactory getInstance() {
        return INSTANCE;
    }
}
