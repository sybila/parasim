/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.application.model;

import org.sybila.parasim.extension.projectmanager.api.Experiment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.PrecisionConfigurationResource;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeVariableMapping;
import org.sybila.parasim.model.sbml.SBMLOdeSystemFactory;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.space.OrthogonalSpaceResource;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResultResource;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FormulaResource;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.model.xml.XMLResource;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExperimentImpl implements Experiment {

    public static final String DEFAULT_TIMEOUT_IN_MINUTES = "10";
    public static final String DEFAULT_ITERATION_LIMIT = "0";
    private OdeSystem odeSystem;
    private FormulaResource stlFormulaResource;
    private OrthogonalSpaceResource initialSpaceResource;
    private OrthogonalSpaceResource simulationSpaceResource;
    private PrecisionConfigurationResource precisionConfigurationResource;
    private VerificationResultResource verificationResultResource;
    private long timeoutAmount;
    private TimeUnit timeoutUnit;
    private int iterationLimit;

    public ExperimentImpl(OdeSystem odeSystem, FormulaResource stlFormulaResource, OrthogonalSpaceResource initialSpaceResource, OrthogonalSpaceResource simulationSpaceResource, PrecisionConfigurationResource precisionConfigurationResource, VerificationResultResource verificationResultResource, long timeoutAmount, TimeUnit timeoutUnit, int iterationLimit) throws XMLException {
        Validate.notNull(odeSystem);
        Validate.notNull(stlFormulaResource);
        Validate.notNull(initialSpaceResource);
        Validate.notNull(simulationSpaceResource);
        Validate.notNull(precisionConfigurationResource);
        Validate.notNull(verificationResultResource);
        // setting
        this.odeSystem = odeSystem;
        this.stlFormulaResource = stlFormulaResource;
        this.initialSpaceResource = initialSpaceResource;
        this.simulationSpaceResource = simulationSpaceResource;
        this.precisionConfigurationResource = precisionConfigurationResource;
        this.verificationResultResource = verificationResultResource;
        this.initialSpaceResource.load();
        this.timeoutAmount = timeoutAmount;
        this.timeoutUnit = timeoutUnit;
        this.iterationLimit = iterationLimit;
        // loading
        this.stlFormulaResource.setVariableMapping(new OdeVariableMapping(this.odeSystem));
        this.stlFormulaResource.load();
        this.simulationSpaceResource.load();
        this.precisionConfigurationResource.load();
        this.initialSpaceResource.load();
    }

    public static ExperimentImpl fromPropertiesFile(String filename) throws IOException {
        Validate.notNull(filename, "File name is not specified.");
        Properties experiment = new Properties();
        File experimentFile = new File(filename);
        InputStream input = null;
        try {
            input = new FileInputStream(experimentFile);
            experiment.load(input);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        Validate.notNull(experiment.getProperty("sbml.file"), "SBML file is not specified. Use [sbml.file] property in the experiment file.");
        Validate.notNull(experiment.getProperty("stl.file"), "STL file is not specified. Use [stl.file] property in the experiment file.");
        Validate.notNull(experiment.getProperty("space.initial.file"), "File containg initial space is not specified. Use [space.initial.file] property in the experiment file.");
        Validate.notNull(experiment.getProperty("space.simulation.file"), "File containg simulation space is not specified. Use [space.simulation.file] property in the experiment file.");
        Validate.notNull(experiment.getProperty("simulation.precision.file"), "File containg precision setting for simulation is not specified. Use [simulation.precision.file] property in the experiment file.");
        Validate.notNull(experiment.getProperty("result.output.file"), "File for exporting results is not specified. Use [result.output.file] property in the experiment file.");
        OdeSystem odeSystem = SBMLOdeSystemFactory.fromFile(getFileWithAbsolutePath(experiment.getProperty("sbml.file"), experimentFile.getParentFile()));
        OrthogonalSpaceResource initialSpaceResource = new OrthogonalSpaceResource(getFileWithAbsolutePath(experiment.getProperty("space.initial.file"), experimentFile.getParentFile()), odeSystem);
        initialSpaceResource.load();
        try {
            return new ExperimentImpl(
                    initialSpaceResource.getRoot().getOdeSystem(),
                    new FormulaResource(getFileWithAbsolutePath(experiment.getProperty("stl.file"), experimentFile.getParentFile())),
                    initialSpaceResource,
                    new OrthogonalSpaceResource(getFileWithAbsolutePath(experiment.getProperty("space.simulation.file"), experimentFile.getParentFile()), initialSpaceResource.getRoot().getOdeSystem()),
                    new PrecisionConfigurationResource(getFileWithAbsolutePath(experiment.getProperty("simulation.precision.file"), experimentFile.getParentFile())),
                    new VerificationResultResource(getFileWithAbsolutePath(experiment.getProperty("result.output.file"), experimentFile.getParentFile())),
                    Long.parseLong(experiment.getProperty("timeout.amount", DEFAULT_TIMEOUT_IN_MINUTES)),
                    TimeUnit.valueOf(experiment.getProperty("timeout.unit", "minutes").toUpperCase()),
                    Integer.parseInt(experiment.getProperty("iteration.limit", DEFAULT_ITERATION_LIMIT)));
        } catch (XMLException e) {
            throw new IOException("Can't load data for experiment,", e);
        }
    }

    @Override
    public OdeSystem getOdeSystem() {
        return odeSystem;
    }

    @Override
    public Formula getFormula() {
        return stlFormulaResource.getRoot();
    }

    @Override
    public OrthogonalSpace getInitialSpace() {
        return initialSpaceResource.getRoot();
    }

    @Override
    public PrecisionConfiguration getPrecisionConfiguration() {
        return precisionConfigurationResource.getRoot();
    }

    @Override
    public OrthogonalSpace getSimulationSpace() {
        return simulationSpaceResource.getRoot();
    }

    @Override
    public long getTimeoutAmount() {
        return timeoutAmount;
    }

    @Override
    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    @Override
    public int getIterationLimit() {
        return iterationLimit;
    }

    @Override
    public XMLResource<VerificationResult> getVerificationResultResource() {
        return verificationResultResource;
    }

    private static File getFileWithAbsolutePath(String filename, File directory) {
        File file = new File(filename);
        return file.isAbsolute() ? file : new File(directory, filename);
    }

}
