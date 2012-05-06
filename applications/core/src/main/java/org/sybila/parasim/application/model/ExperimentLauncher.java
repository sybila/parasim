package org.sybila.parasim.application.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.model.ode.OdeVariableMapping;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExperimentLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentLauncher.class);

    private ExperimentLauncher() {
    }

    public static VerificationResult launch(Manager manager, Experiment experiment) throws Exception {
        ComputationContainer container = manager.resolve(ComputationContainer.class, Default.class, manager.getRootContext());
        experiment.getPrecisionConfigurationResources().load();
        experiment.getInitialSamplingResource().load();
        experiment.getSimulationSpaceResource().load();
        experiment.getInitialSpaceResource().load();
        experiment.getSTLFormulaResource().setVariableMapping(new OdeVariableMapping(experiment.getOdeSystem()));
        experiment.getSTLFormulaResource().load();
        return container.compute(new ValidityRegionsComputation(
                experiment.getOdeSystem(),
                experiment.getPrecisionConfigurationResources().getRoot(),
                experiment.getInitialSamplingResource().getRoot(),
                experiment.getSimulationSpaceResource().getRoot(),
                experiment.getInitialSpaceResource().getRoot(),
                experiment.getSTLFormulaResource().getRoot())).get();
    }

}
