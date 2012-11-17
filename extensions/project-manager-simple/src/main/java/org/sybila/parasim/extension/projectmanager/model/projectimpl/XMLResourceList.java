package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.extension.projectmanager.model.project.ExperimentResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.ResourceAction;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.model.xml.XMLRepresentable;
import org.sybila.parasim.model.xml.XMLResource;
import org.sybila.parasim.util.SimpleLock;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class XMLResourceList<E extends XMLRepresentable> implements ExperimentResourceList<E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLResourceList.class);

    private class Resource {

        private SimpleLock used;
        private XMLResource<E> resource;

        public Resource(XMLResource<E> resource) {
            if (resource == null) {
                throw new IllegalArgumentException("Argument (resource) null.");
            }
            used = new SimpleLock();
            this.resource = resource;
        }

        public Resource(XMLResource<E> resource, Resource src) {
            if (resource == null) {
                throw new IllegalArgumentException("Argument (resource) null.");
            }
            used = src.used;
            this.resource = resource;
        }

        public XMLResource<E> get() {
            return resource;
        }

        public boolean isUsed() {
            return !used.isAccessible();
        }

        public void addExperiment() {
            used.lock();
        }

        public void removeExperiment() {
            used.unlock();
        }
    }
    //
    private final DirProject project;
    private final FileManager files;
    private boolean saved = true;
    private final Map<String, Resource> resources = new HashMap<>();

    public XMLResourceList(DirProject parent, ExperimentSuffixes suffix) {
        if (parent == null) {
            throw new IllegalArgumentException("Argument (parent) is null.");
        }
        if (suffix == null) {
            throw new IllegalArgumentException("Argument (suffix) is null.");
        }
        project = parent;
        files = new FileManager(parent.getProjectDirectory(), suffix);
    }

    public void loadResources() {
        resources.clear();
        for (String name : files.getFiles()) {
            if (!resources.containsKey(name)) {
                Resource resource = new Resource(getXMLResource(files.getFile(name)));
                try {
                    resource.get().load();
                    resources.put(name, resource);
                } catch (XMLException xmle) {
                    LOGGER.warn("Unable to load `" + files.getFile(name) + "'.", xmle);
                }
            }
        }
    }

    protected abstract XMLResource<E> getXMLResource(File target);

    protected abstract DirProject.ExperimentAction getAction(String name, String newName);

    public void save() throws ResourceException {
        for (Resource resource : resources.values()) {
            try {
                resource.get().store();
            } catch (XMLException xmle) {
                throw new ResourceException("Unable to save resources.", xmle);
            }
        }
        saved = true;
    }

    public boolean isSaved() {
        return saved;
    }

    public void addExperiment(String name) {
        resources.get(name).addExperiment();
    }

    public void removeExperiment(String name) {
        resources.get(name).removeExperiment();
    }

    protected DirProject getParent() {
        return project;
    }

    private class AddAction extends CreateFile {

        private E target;

        public AddAction(E target, String name) {
            super(files, name);
            this.target = target;
        }

        @Override
        public void commit() {
            if (isNotUsed()) {
                super.commit();
                Resource resource = new Resource(getXMLResource(getFile()));
                resource.get().setRoot(target);
                resources.put(getName(), resource);
                saved = false;
            }
        }
    }

    @Override
    public Set<String> getNames() {
        return Collections.unmodifiableSet(resources.keySet());
    }

    @Override
    public ResourceAction add(String name, E target) {
        if (resources.containsKey(name)) {
            return null;
        }
        return new AddAction(target, name);
    }

    @Override
    public E get(String name) {
        return resources.get(name).get().getRoot();
    }

    @Override
    public boolean isUsedInExperiment(String name) {
        return resources.get(name).isUsed();
    }

    @Override
    public void put(String name, E target) {
        resources.get(name).get().setRoot(target);
        saved = false;
    }

    @Override
    public void remove(String name) {
        if (resources.containsKey(name)) {
            if (resources.remove(name).isUsed()) {
                project.applyAction(getAction(name, null));
            }
            files.deleteFile(name);
        }
    }

    private class RenameAction extends CreateFile {

        private String oldName;

        public RenameAction(String oldName, String name) {
            super(files, name);
            this.oldName = oldName;
        }

        @Override
        public void commit() {
            if (isNotUsed()) {
                super.commit();
                Resource old = resources.get(oldName);
                Resource resource = new Resource(getXMLResource(getFile()), old);
                resource.get().setRoot(old.get().getRoot());
                resources.put(getName(), resource);
                removeAndRename(oldName, getName());
                saved = false;
            }
        }
    }

    private void removeAndRename(String name, String newName) {
        if (resources.remove(name).isUsed()) {
            project.applyAction(getAction(name, newName));
        }
        files.deleteFile(name);
    }

    @Override
    public ResourceAction rename(String name, String newName) {
        if (!resources.containsKey(name)) {
            return null;
        }
        if (resources.containsKey(newName)) {
            return null;
        }
        return new RenameAction(name, newName);
    }
}
