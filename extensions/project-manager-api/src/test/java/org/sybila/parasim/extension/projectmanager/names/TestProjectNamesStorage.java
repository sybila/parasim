package org.sybila.parasim.extension.projectmanager.names;

import org.sybila.parasim.extension.projectmanager.names.ProjectNamesFactory;
import org.sybila.parasim.extension.projectmanager.names.ProjectResource;
import org.sybila.parasim.extension.projectmanager.names.ProjectNames;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import org.sybila.parasim.model.xml.FileXMLResource;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestProjectNamesStorage {

    private void addAll(Collection<String> target, String[] source) {
        target.addAll(Arrays.asList(source));
    }

    private ProjectNames getTestProjectNames() {
        // note -- used random name generator to do this

        ProjectNames result = new ProjectNames();
        result.setModelName("testModel.xml");
        addAll(result.getFormulaeNames(), new String[]{"Tameka Sutherlin", "Hugh Knudtson", "Kelly Craighead"});
        //list of initial spaces names is intentionally empty
        addAll(result.getSimulationSpacesNames(), new String[]{"Loraine Coletta", "Jamie Gills"});
        addAll(result.getPrecisionConfigurationsNames(), new String[]{"Max Mccook"});
        addAll(result.getInitialSamplingsNames(), new String[]{"Horney", "Odessa Juliano", "Javier Nogueira"});
        addAll(result.getExperimentsNames(), new String[]{"Marylou Bufford", "Darcy Schuh"});
        result.setActiveExperiment("Darcy Schuh");
        return result;
    }

    private static class Resource extends FileXMLResource<ProjectNames> {

        public Resource(File file) {
            super(file);
        }

        @Override
        protected XMLRepresentableFactory<ProjectNames> getFactory() {
            return ProjectNamesFactory.getInstance();
        }

        @Override
        protected String getNamespace() {
            return "http://www.sybila.org/parasim/project";
        }

        @Override
        protected URL getXMLSchema() {
            return ProjectResource.class.getClassLoader().getResource("project.xsd");
        }
    }

    @Test
    public void tryLoad() {
        Resource resource = null;
        try {
            resource = new Resource(new File(getClass().getClassLoader().getResource("testProject.xml").toURI()));
        } catch (URISyntaxException urise) {
            urise.printStackTrace();
            Assert.fail("Cannot find test project file.");
        }
        try {
            resource.load();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.printStackTrace();
            }
            Assert.fail("XML error: " + xmle.getMessage());
        }
        Assert.assertEquals(resource.getRoot(), getTestProjectNames(), "ProjectNames should load correctly.");
    }

    @Test
    public void tryStoreLoad() {
        File temp = null;
        try {
            temp = File.createTempFile("project", ".xml");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Assert.fail("Temporary file could not be created.");
        }
        temp.deleteOnExit();

        //store
        Resource resource = new Resource(temp);
        resource.setRoot(getTestProjectNames());
        try {
            resource.store();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            Assert.fail("XML error while storing: " + xmle.getMessage());
        }

        //reset
        resource.setRoot(null);

        //load
        try {
            resource.load();
        } catch (XMLException xmle) {
            if (xmle.getCause() != null) {
                xmle.getCause().printStackTrace();
            }
            Assert.fail("XML error while loading: " + xmle.getMessage());
        }

        Assert.assertEquals(resource.getRoot(), getTestProjectNames(), "After being stored and loaded, the project names should not change.");
    }
}
