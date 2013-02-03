/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

        private Resource resource;

        public AddAction(E target, String name) {
            super(files, name);
            if (super.isViable()) {
                resource = new Resource(getXMLResource(getFile()));
                resource.get().setRoot(target);
                try {
                    resource.get().store();
                } catch (XMLException xmle) {
                    LOGGER.warn("Unable to store new resource.", xmle);
                    resource = null;
                    revert();
                }
            }

        }

        @Override
        public boolean isViable() {
            return super.isViable() && (resource != null);
        }

        @Override
        public void commit() {
            if (isNotUsed() && isViable()) {
                super.commit();
                resources.put(getName(), resource);
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
        private Resource resource;

        public RenameAction(String oldName, String name) {
            super(files, name);
            if (super.isViable()) {
                Resource old = resources.get(oldName);
                resource = new Resource(getXMLResource(getFile()), old);
                resource.get().setRoot(old.get().getRoot());
                try {
                    resource.get().store();
                } catch (XMLException xmle) {
                    LOGGER.warn("Unable to store resource to new file.", xmle);
                    resource = null;
                    revert();
                }
            }
            this.oldName = oldName;
        }

        @Override
        public boolean isViable() {
            return super.isViable() && (resource != null);
        }

        @Override
        public void commit() {
            if (isNotUsed() && isViable()) {
                super.commit();
                resources.put(getName(), resource);
                removeAndRename(oldName, getName());
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
