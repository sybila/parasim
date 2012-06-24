/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.density.api.InitialSamplingResource;
import org.sybila.parasim.computation.simulation.api.PrecisionConfigurationResource;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.sbml.SBMLOdeSystemFactory;
import org.sybila.parasim.model.space.OrthogonalSpaceResource;
import org.sybila.parasim.model.verification.result.VerificationResultResource;
import org.sybila.parasim.model.verification.stl.FormulaResource;
import org.sybila.parasim.model.xml.XMLException;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExperimentImpl implements Experiment {

    public static final String DEFAULT_TIMEOUT_IN_MILLISECONDS = "30000";

    private OdeSystem odeSystem;
    private FormulaResource stlFormulaResource;
    private OrthogonalSpaceResource initialSpaceResource;
    private OrthogonalSpaceResource simulationSpaceResource;
    private PrecisionConfigurationResource precisionConfigurationResource;
    private InitialSamplingResource initialSamplingResource;
    private VerificationResultResource verificationResultResource;
    private long timeoutInMilliSeconds;

    public ExperimentImpl(OdeSystem odeSystem, FormulaResource stlFormulaResource, OrthogonalSpaceResource initialSpaceResource, OrthogonalSpaceResource simulationSpaceResource, PrecisionConfigurationResource precisionConfigurationResource, InitialSamplingResource initialSamplingResource, VerificationResultResource verificationResultResource, long timeoutInMilliSeconds) throws XMLException {
        Validate.notNull(odeSystem);
        Validate.notNull(stlFormulaResource);
        Validate.notNull(initialSpaceResource);
        Validate.notNull(simulationSpaceResource);
        Validate.notNull(precisionConfigurationResource);
        Validate.notNull(initialSamplingResource);
        Validate.notNull(verificationResultResource);
        this.odeSystem = odeSystem;
        this.stlFormulaResource = stlFormulaResource;
        this.initialSpaceResource = initialSpaceResource;
        this.simulationSpaceResource = simulationSpaceResource;
        this.precisionConfigurationResource = precisionConfigurationResource;
        this.initialSamplingResource = initialSamplingResource;
        this.verificationResultResource = verificationResultResource;
        this.initialSamplingResource.load();
        this.initialSpaceResource.load();
        this.timeoutInMilliSeconds = timeoutInMilliSeconds;
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
        Validate.notNull(experiment.getProperty("density.sampling.file"), "File containg setting for initial spawning is not specified. Use [density.sampling.file] property in the experiment file.");
        Validate.notNull(experiment.getProperty("result.output.file"), "File for exporting results is not specified. Use [result.output.file] property in the experiment file.");
        try {
            return new ExperimentImpl(
                    SBMLOdeSystemFactory.fromFile(getFileWithAbsolutePath(experiment.getProperty("sbml.file"), experimentFile.getParentFile())),
                    new FormulaResource(getFileWithAbsolutePath(experiment.getProperty("stl.file"), experimentFile.getParentFile())),
                    new OrthogonalSpaceResource(getFileWithAbsolutePath(experiment.getProperty("space.initial.file"), experimentFile.getParentFile())),
                    new OrthogonalSpaceResource(getFileWithAbsolutePath(experiment.getProperty("space.simulation.file"), experimentFile.getParentFile())),
                    new PrecisionConfigurationResource(getFileWithAbsolutePath(experiment.getProperty("simulation.precision.file"), experimentFile.getParentFile())),
                    new InitialSamplingResource(getFileWithAbsolutePath(experiment.getProperty("density.sampling.file"), experimentFile.getParentFile())),
                    new VerificationResultResource(getFileWithAbsolutePath(experiment.getProperty("result.output.file"), experimentFile.getParentFile())),
                    Long.parseLong(experiment.getProperty("timeout", DEFAULT_TIMEOUT_IN_MILLISECONDS))
            );
        } catch(XMLException e) {
            throw new IOException("Can't load data for experiment,", e);
        }
    }

    public OdeSystem getOdeSystem() {
        return odeSystem;
    }

    public FormulaResource getSTLFormulaResource() {
        return stlFormulaResource;
    }

    public OrthogonalSpaceResource getInitialSpaceResource() {
        return initialSpaceResource;
    }

    public OrthogonalSpaceResource getSimulationSpaceResource() {
        return simulationSpaceResource;
    }

    public PrecisionConfigurationResource getPrecisionConfigurationResources() {
        return precisionConfigurationResource;
    }

    public InitialSamplingResource getInitialSamplingResource() {
        return initialSamplingResource;
    }

    public VerificationResultResource getVerificationResultResource() {
        return verificationResultResource;
    }

    private static File getFileWithAbsolutePath(String filename, File directory) {
        File file = new File(filename);
        return file.isAbsolute() ? file : new File(directory, filename);
    }

    @Override
    public long getTimeoutInMilliSeconds() {
        return timeoutInMilliSeconds;
    }
}
