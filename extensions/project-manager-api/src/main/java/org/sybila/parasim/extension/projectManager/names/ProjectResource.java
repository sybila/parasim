package org.sybila.parasim.extension.projectManager.names;

import org.sybila.parasim.extension.projectManager.project.ProjectFactory;
import java.io.File;
import java.net.URL;
import org.sybila.parasim.extension.projectManager.project.Project;
import org.sybila.parasim.model.xml.FileXMLResource;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Node;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class ProjectResource extends FileXMLResource<Project> {

    private XMLRepresentableFactory<Project> projectFactory = new XMLRepresentableFactory<Project>() {

        @Override
        public Project getObject(Node source) throws XMLFormatException {
            ProjectNames names = ProjectNamesFactory.getInstance().getObject(source);
            return getProjectFactory().getProject(names);
        }
    };

    public ProjectResource(File file) {
        super(file);
    }

    @Override
    protected XMLRepresentableFactory<Project> getFactory() {
        return projectFactory;
    }

    protected abstract ProjectFactory getProjectFactory();

    @Override
    protected String getNamespace() {
        return "http://www.sybila.org/parasim/project";
    }

    @Override
    protected URL getXMLSchema() {
        return getClass().getClassLoader().getResource("project.xsd");
    }
}
