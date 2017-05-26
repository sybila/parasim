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
package org.sybila.parasim.extension.projectmanager.names;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ExperimentNamesResource {

    private File target;
    private ExperimentNames root = null;

    public ExperimentNamesResource(File targetFile) {
        if (targetFile == null) {
            throw new IllegalArgumentException("The parameter [file] is null.");
        }
        target = targetFile;
    }

    public ExperimentNames getRoot() {
        return root;
    }

    public File getTargetFile() {
        return target;
    }

    public void setRoot(ExperimentNames target) {
        root = target;
    }

    private void close(Closeable target) throws ResourceException {
        try {
            target.close();
        } catch (IOException ioe) {
            throw new ResourceException("Unable to close experiment resource.", ioe);
        }
    }

    public void load() throws ResourceException {
        InputStream in = null;
        Properties experiment = new Properties();
        try {
            in = new FileInputStream(target);
            experiment.load(in);
        } catch (IOException ioe) {
            throw new ResourceException("Unable to load from experiment resource.", ioe);
        } finally {
            close(in);
        }
        root = ExperimentNames.Factory.getInstance().getExperimentNames(experiment);
    }

    public void store() throws ResourceException {
        if (root == null) {
            throw new ResourceException("There is nothing to store (root is null).");
        }
        Properties experiment = ExperimentNames.Factory.getInstance().getProperties(root);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(target);
            experiment.store(out, null);
        } catch (IOException ioe) {
            throw new ResourceException("Unable to store to experiment resource.", ioe);
        } finally {
            close(out);
        }
    }
}
