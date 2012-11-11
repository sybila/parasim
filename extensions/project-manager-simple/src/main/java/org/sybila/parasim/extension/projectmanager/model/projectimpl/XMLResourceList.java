package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import java.util.Map;
import org.sybila.parasim.extension.projectmanager.model.project.ExperimentResourceList;
import org.sybila.parasim.extension.projectmanager.model.project.ResourceAction;
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
    private DirProject project;
    private FileManager manager;
    private boolean saved = true;
    private Map<String, Resource> resources;

    protected abstract XMLResource<E> getXMLResource(File target);

    protected abstract DirProject.ExperimentAction getAction(final String name, final String newName);

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

    private class AddAction extends CreateFile {

        private E target;

        public AddAction(E target, FileManager manager, String name) {
            super(manager, name);
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
    public ResourceAction add(String name, E target) {
        if (resources.containsKey(name)) {
            return null;
        }
        return new AddAction(target, manager, name);
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
            manager.deleteFile(name);
        }
    }

    private class RenameAction extends CreateFile {

        private String oldName;

        public RenameAction(String oldName, FileManager manager, String name) {
            super(manager, name);
            this.oldName = oldName;
        }

        @Override
        public void commit() {
            if (isNotUsed()) {
                super.commit();
                Resource old = resources.get(oldName);
                Resource resource = new Resource(getXMLResource(getFile()), old);
                resource.get().setRoot(old.get().getRoot());
                removeAndRename(oldName, getName());
                saved = false;
            }
        }
    }

    private void removeAndRename(String name, String newName) {
        if (resources.remove(name).isUsed()) {
            project.applyAction(getAction(name, newName));
        }
    }

    @Override
    public ResourceAction rename(String name, String newName) {
        if (!resources.containsKey(name)) {
            return null;
        }
        if (resources.containsKey(newName)) {
            return null;
        }
        return new RenameAction(name, manager, newName);
    }
}
