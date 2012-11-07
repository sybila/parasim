package org.sybila.parasim.extension.projectmanager.oldoroject;

import java.io.File;
import java.io.IOException;
import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.PrecisionConfigurationResource;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNamesResource;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.extension.projectmanager.names.ProjectNames;
import org.sybila.parasim.extension.projectmanager.project.AbstractProject;
import org.sybila.parasim.extension.projectmanager.project.Project;
import org.sybila.parasim.extension.projectmanager.project.ProjectFactory;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.extension.projectmanager.project.ResourceList;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeVariableMapping;
import org.sybila.parasim.model.sbml.SBMLOdeSystemFactory;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FormulaResource;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
 */
public class DirProject extends AbstractProject implements Project {

    private File dir;
    private String odeName;
    private OdeSystem model;
    //
    private DirResourceList<Formula, FileXMLResourceAdapter<Formula, FormulaResource>> formulae;
    private DirResourceList<ExperimentNames, ExperimentNamesResourceAdapter> experiments;
    private DirResourceList<PrecisionConfiguration, FileXMLResourceAdapter<PrecisionConfiguration, PrecisionConfigurationResource>> precisions;

    private DirProject(File directory, String odeName, OdeSystem model) {
        dir = directory;
        this.odeName = odeName;
        this.model = model;
        formulae = new DirResourceList<>(directory, ExperimentSuffixes.FORMULA, new FileResource.Factory<Formula, FileXMLResourceAdapter<Formula, FormulaResource>>() {

            @Override
            public FileXMLResourceAdapter<Formula, FormulaResource> get(File target) {
                FormulaResource resource = new FormulaResource(target);
                resource.setVariableMapping(new OdeVariableMapping(DirProject.this.model));
                return new FileXMLResourceAdapter<>(resource);
            }
        });
        experiments = new DirResourceList<>(directory, ExperimentSuffixes.EXPERIMENT, new FileResource.Factory<ExperimentNames, ExperimentNamesResourceAdapter>() {

            @Override
            public ExperimentNamesResourceAdapter get(File target) {
                return new ExperimentNamesResourceAdapter(new ExperimentNamesResource(target));
            }
        });
        precisions = new DirResourceList<>(directory, ExperimentSuffixes.PRECISION_CONFIGURATION, new FileResource.Factory<PrecisionConfiguration, FileXMLResourceAdapter<PrecisionConfiguration, PrecisionConfigurationResource>>() {

            @Override
            public FileXMLResourceAdapter<PrecisionConfiguration, PrecisionConfigurationResource> get(File target) {
                return new FileXMLResourceAdapter<>(new PrecisionConfigurationResource(target));
            }
        });
    }

    private File getFile(String name, ExperimentSuffixes suffix) {
        if (suffix != null) {
            name = suffix.add(name);
        }
        return new File(dir, name);
    }

    @Override
    public String getActiveExperiment() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Experiment getExperiment(ExperimentNames names) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResourceList<ExperimentNames> getExperimentList() {
        return experiments;
    }

    @Override
    public ResourceList<Formula> getFormulae() {
        return formulae;
    }

    @Override
    public ResourceList<InitialSampling> getInitialSamplings() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResourceList<OrthogonalSpace> getInitialSpaces() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getModelName() {
        return odeName;
    }

    @Override
    public OdeSystem getOdeSystem() {
        return model;
    }

    @Override
    public ResourceList<PrecisionConfiguration> getPrecisionConfigurations() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResourceList<OrthogonalSpace> getSimulationSpaces() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ResourceList<VerificationResult> getVerificationResults() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static class Factory implements ProjectFactory {

        private File dir;

        public Factory(File dir) {
            if (dir == null) {
                throw new IllegalArgumentException("Argument (dir) is null.");
            }
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("Argument (dir) is not a directory.");
            }
            this.dir = dir;
        }

        @Override
        public Project getProject(ProjectNames names) throws ResourceException {
            //load model
            OdeSystem model = null;
            try {
                model = SBMLOdeSystemFactory.fromFile(new File(dir, ExperimentSuffixes.MODEL.add(names.getModelName())));
            } catch (IOException ioe) {
                throw new ResourceException("Unable to load model.", ioe);
            }

            DirProject result = new DirProject(dir, names.getModelName(), model);

            //load formulae

            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
