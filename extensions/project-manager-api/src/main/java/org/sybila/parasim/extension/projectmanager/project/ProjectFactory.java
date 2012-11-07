package org.sybila.parasim.extension.projectmanager.project;

import org.sybila.parasim.extension.projectmanager.names.ProjectNames;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
 */
public interface ProjectFactory {

    public Project getProject(ProjectNames names) throws ResourceException;
}