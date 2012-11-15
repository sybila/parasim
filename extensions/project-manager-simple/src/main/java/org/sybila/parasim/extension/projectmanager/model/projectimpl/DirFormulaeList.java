package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.extension.projectmanager.model.project.FormulaResourceList;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.model.ode.OdeVariableMapping;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FormulaResource;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.util.SimpleLock;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class DirFormulaeList implements FormulaResourceList {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirFormulaeList.class);

    private class Resource {

        private SimpleLock unused;
        private FormulaResource resource;

        public Resource(FormulaResource resource) {
            this.resource = resource;
            unused = new SimpleLock();
        }

        public Resource(FormulaResource resource, Resource src) {
            this.resource = resource;
            unused = src.unused;
        }

        public FormulaResource get() {
            return resource;
        }

        public boolean isUsed() {
            return !unused.isAccessible();
        }

        public void addExperiment() {
            unused.lock();
        }

        public void removeExperiment() {
            unused.unlock();
        }
    }
    //
    private final DirProject project;
    private final FileManager files;
    private final Map<String, Resource> resources = new HashMap<>();

    public DirFormulaeList(DirProject parent) {
        if (parent == null) {
            throw new IllegalArgumentException("Argument (parent) is null.");
        }
        project = parent;
        files = new FileManager(parent.getProjectDirectory(), ExperimentSuffixes.FORMULA);
    }

    public void loadResources() {
        resources.clear();
        for (String name : files.getFiles()) {
            if (!resources.containsKey(name)) {
                Resource resource = new Resource(createResource(files.getFile(name)));
                try {
                    resource.get().load();
                    resources.put(name, resource);
                } catch (XMLException xmle) {
                    LOGGER.warn("Unable to load `" + files.getFile(name) + "'.", xmle);
                }
            }
        }
    }

    public void addExperiment(String name) {
        resources.get(name).addExperiment();
    }

    public void removeExperiment(String name) {
        resources.get(name).removeExperiment();
    }

    private FormulaResource createResource(File target) {
        FormulaResource result = new FormulaResource(target);
        result.setVariableMapping(new OdeVariableMapping(project.getOdeSystem()));
        return result;
    }

    private boolean storeFormula(Resource target) {
        try {
            target.get().store();
        } catch (XMLException xmle) {
            LOGGER.error("Unable to store formula.", xmle);
            return false;
        }
        return true;
    }

    public Formula get(String name) {
        Resource target = resources.get(name);
        if (target == null) {
            return null;
        }
        return target.get().getRoot();
    }

    @Override
    public Set<String> getNames() {
        return Collections.unmodifiableSet(resources.keySet());
    }

    @Override
    public boolean add(String name, File target) {
        if (resources.containsKey(name)) {
            return false;
        }

        FormulaResource src = createResource(target);
        try {
            src.load();
        } catch (XMLException xmle) {
            LOGGER.warn("Unable to read formula, possibly invalid formula file.", xmle);
            return false;
        }

        if (src.getRoot() == null) {
            LOGGER.warn("Formula not loaded.");
            return false;
        }

        File targetFile = files.createFile(name);
        if (targetFile == null) {
            return false;
        }

        Resource resource = new Resource(createResource(targetFile));
        resource.get().setRoot(src.getRoot());
        if (storeFormula(resource)) {
            resources.put(name, resource);
            return true;
        } else {
            files.deleteFile(name);
            return false;
        }
    }

    @Override
    public boolean isUsedInExperiment(String name) {
        return resources.get(name).isUsed();
    }

    @Override
    public void remove(String name) {
        Resource target = resources.remove(name);
        if (target != null) {
            if (target.isUsed()) {
                project.removeFormula(name);
            }
            files.deleteFile(name);
        }
    }

    @Override
    public boolean rename(String name, String newName) {
        if (resources.containsKey(newName)) {
            return false;
        }

        if (!resources.containsKey(name)) {
            return false;
        }


        File targetFile = files.createFile(newName);
        if (targetFile == null) {
            return false;
        }

        Resource src = resources.get(name);
        Resource target = new Resource(createResource(targetFile), src);
        target.get().setRoot(src.get().getRoot());
        if (storeFormula(target)) {
            resources.remove(name);
            if (target.isUsed()) {
                project.renameFormula(name, newName);
            }
            resources.put(newName, target);
            files.deleteFile(name);
            return true;
        } else {
            files.deleteFile(newName);
            return false;
        }
    }
}
