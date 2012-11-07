package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import org.sybila.parasim.extension.projectmanager.model.project.ResourceAction;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class CreateFile implements ResourceAction {

    private final FileManager manager;
    private final String name;
    private boolean unUsed = true;

    public CreateFile(FileManager manager, String name) {
        if (manager == null) {
            throw new IllegalArgumentException("Argument (manager) is null.");
        }
        if (name == null) {
            throw new IllegalArgumentException("Argument (name) is null.");
        }
        this.manager = manager;
        if (null != manager.createFile(name)) {
            this.name = name;
        } else {
            this.name = null;
        }
    }

    protected File getFile() {
        if (!isViable()) {
            return null;
        }
        return manager.getFile(name);
    }

    @Override
    public boolean isViable() {
        return (name != null);
    }

    @Override
    public void revert() {
        if (isViable() && unUsed) {
            manager.deleteFile(name);
            unUsed = false;
        }
    }

    @Override
    public void commit() {
        unUsed = false;
    }

    protected boolean isNotUsed() {
        return unUsed;
    }

    protected String getName() {
        return name;
    }
}