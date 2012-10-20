package org.sybila.parasim.extension.projectManager.impl;

import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.extension.projectManager.api.ProjectManager;
import org.sybila.parasim.extension.projectManager.api.ProjectManagerRegistered;
import org.sybila.parasim.extension.projectManager.view.PhonyProjectManager;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectManagerRegistrar {

    @Inject
    private Event<ProjectManagerRegistered> event;

    @Provide
    public ProjectManager register() {
        fireEvent();
        return new PhonyProjectManager();
    }

    private void fireEvent() {
        event.fire(new ProjectManagerRegistered());
    }
}
