/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.application.model;

import org.sybila.parasim.computation.density.api.ArrayInitialSampling;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.api.InitialSamplingResource;
import org.sybila.parasim.computation.simulation.api.PrecisionConfigurationResource;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpaceResource;
import org.sybila.parasim.model.verification.stl.FormulaResource;
import org.sybila.parasim.model.xml.XMLException;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestExperimentLauncher {

    private Manager manager;
    private Experiment experiment;

    @BeforeMethod
    public void startManager() {
        try {
            manager = ManagerImpl.create();
        } catch (Exception e) {
            fail("Cannot start manager.", e);
        }
        manager.start();
    }

    @BeforeMethod
    public void createExperiment() {
        OdeSystem odeSystem = null;
        try {
            experiment = new ExperimentImpl(odeSystem,
                new FormulaResource(null),
                new OrthogonalSpaceResource(null),
                new OrthogonalSpaceResource(null),
                new PrecisionConfigurationResource(null),
                new InitialSamplingResource(null),
                null /* no result */, 480000, 10);
        } catch (XMLException xmle) {

        }
    }
}
