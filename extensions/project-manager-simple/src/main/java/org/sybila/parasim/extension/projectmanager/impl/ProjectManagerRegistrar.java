package org.sybila.parasim.extension.projectmanager.impl;

import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.extension.projectmanager.api.ProjectManager;
import org.sybila.parasim.extension.projectmanager.api.ProjectManagerRegistered;
import org.sybila.parasim.extension.projectmanager.view.frame.ProjectManagerWindow;

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
        return new ProjectManagerWindow();
    }

    private void fireEvent() {
        event.fire(new ProjectManagerRegistered());
    }
}
