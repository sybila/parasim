package org.sybila.parasim.extension.projectManager.project;

import org.sybila.parasim.extension.projectManager.names.ProjectNames;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ProjectFactory {

    public Project getProject(ProjectNames names);
}