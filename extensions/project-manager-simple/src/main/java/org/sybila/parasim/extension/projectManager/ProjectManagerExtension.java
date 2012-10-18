package org.sybila.parasim.extension.projectManager;

import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;
import org.sybila.parasim.extension.projectManager.impl.ProjectManagerRegistrar;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectManagerExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.extension(ProjectManagerRegistrar.class);
    }
}
