/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
        if (name == null) {
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