package org.sybila.parasim.extension.projectManager.project;

import org.sybila.parasim.extension.projectManager.names.ProjectNames;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class AbstractProject implements Project {

    @Override
    public Element toXML(Document doc) {
        return ProjectNames.getFromProject(this).toXML(doc);
    }
}
