package org.sybila.parasim.extension.projectManager.project;

import org.sybila.parasim.extension.projectManager.names.ProjectNames;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
 */
public interface ProjectFactory {

    public Project getProject(ProjectNames names) throws ResourceException;
}