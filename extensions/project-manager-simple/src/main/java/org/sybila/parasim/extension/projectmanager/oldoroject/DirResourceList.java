package org.sybila.parasim.extension.projectmanager.oldoroject;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;
import org.sybila.parasim.extension.projectmanager.project.ResourceList;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
 */
public class DirResourceList<T, R extends FileResource<T>> implements ResourceList<T> {

    private File parent;
    private ExperimentSuffixes suffix;
    private FileResource.Factory<T, R> factory;
    private Map<String, R> resources = new HashMap<>();

    public DirResourceList(File parentDir, ExperimentSuffixes suffix, FileResource.Factory<T, R> resourceFactory) {
        if (parentDir == null) {
            throw new IllegalArgumentException("Argument (parent dir) is null.");
        }
        if (!parentDir.isDirectory()) {
            throw new IllegalArgumentException("Parent directory is not a directory.");
        }
        parent = parentDir;
        if (resourceFactory == null) {
            throw new IllegalArgumentException("Argument (resource factory) is null.");
        }
        factory = resourceFactory;
        this.suffix = suffix;
    }

    private File createPath(String baseName) {
        if (suffix != null) {
            baseName = suffix.add(baseName);
        }
        return new File(parent, baseName);
    }

    @Override
    public boolean add(String name, T root) {
        if (resources.containsKey(name)) {
            return false;
        }
        File newFile = createPath(name);
        try {
            if (!newFile.createNewFile()) {
                return false;
            }
        } catch (IOException ioe) {
            return false;
        }

        R resource = factory.get(newFile);
        resources.put(name, resource);
        resource.setRoot(root);
        return true;
    }

    @Override
    public T get(String name) {
        return resources.get(name).getRoot();
    }

    @Override
    public Iterable<String> getNames() {
        return Collections.unmodifiableSet(resources.keySet());
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }

    @Override
    public void load(String name) throws ResourceException {
        FileResource<T> resource = resources.get(name);
        if (resource != null) {
            resource.load();
        }
    }

    @Override
    public void remove(String name) {
        R resource = resources.remove(name);
        if (resource != null) {
            resource.getFile().delete();
        }
    }

    @Override
    public boolean rename(String name, String newName) {
        File path = createPath(newName);
        R resource = resources.get(name);
        return resource.getFile().renameTo(path);
    }

    @Override
    public void store(String name) throws ResourceException {
        R resource = resources.get(name);
        if (resource != null) {
            resource.store();
        }
    }

    @Override
    public int size() {
        return resources.size();
    }

    public R getResource(String name) {
        return resources.get(name);
    }

    public void addExisting(String name) throws ResourceException {
        if (resources.containsKey(name)) {
            throw new ResourceException("Cannot add load resource: resource with given name already exists.");
        }

        R resource = factory.get(createPath(name));
        resource.setRoot(null);
        resources.put(name, resource);
    }
}