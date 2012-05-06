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

    private OdeSystem odeSystem;
    private FormulaResource stlFormulaResource;
    private OrthogonalSpaceResource initialSpaceResource;
    private OrthogonalSpaceResource simulationSpaceResource;
    private PrecisionConfigurationResource precisionConfigurationResource;
    private InitialSamplingResource initialSamplingResource;
    private VerificationResultResource verificationResultResource;

    public ExperimentImpl(OdeSystem odeSystem, FormulaResource stlFormulaResource, OrthogonalSpaceResource initialSpaceResource, OrthogonalSpaceResource simulationSpaceResource, PrecisionConfigurationResource precisionConfigurationResource, InitialSamplingResource initialSamplingResource, VerificationResultResource verificationResultResource) throws XMLException {
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
                    new VerificationResultResource(getFileWithAbsolutePath(experiment.getProperty("result.output.file"), experimentFile.getParentFile())));
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
        return null;
    }

    private static File getFileWithAbsolutePath(String filename, File directory) {
        File file = new File(filename);
        return file.isAbsolute() ? file : new File(directory, filename);
    }
}
