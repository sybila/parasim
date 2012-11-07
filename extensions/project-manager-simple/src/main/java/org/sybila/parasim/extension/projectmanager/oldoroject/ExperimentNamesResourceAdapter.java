package org.sybila.parasim.extension.projectmanager.oldoroject;

import java.io.File;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNamesResource;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
 */
public class ExperimentNamesResourceAdapter implements FileResource<ExperimentNames> {

    private ExperimentNamesResource resource;

    public ExperimentNamesResourceAdapter(ExperimentNamesResource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Argument (experiment names resource) is null.");
        }
        this.resource = resource;
    }

    @Override
    public File getFile() {
        return resource.getTargetFile();
    }

    @Override
    public ExperimentNames getRoot() {
        return resource.getRoot();
    }

    @Override
    public void load() throws ResourceException {
        resource.load();
    }

    @Override
    public void setRoot(ExperimentNames target) {
        resource.setRoot(target);
    }

    @Override
    public void store() throws ResourceException {
        resource.load();
    }

    public static class Factory implements FileResource.Factory<ExperimentNames, FileResource<ExperimentNames>> {

        @Override
        public FileResource<ExperimentNames> get(File target) {
            return new ExperimentNamesResourceAdapter(new ExperimentNamesResource(target));
        }
    }
}
