/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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
import java.io.FilenameFilter;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);
    private final File dir;
    private final ExperimentSuffixes suffix;

    public FileManager(File directory, ExperimentSuffixes fileExtension) {
        if (directory == null) {
            throw new IllegalArgumentException("Argument (directory) is null.");
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Target file is not a directory.");
        }
        dir = directory;
        suffix = fileExtension;
    }

    public File getFile(String name) {
        if (suffix != null) {
            name = suffix.add(name);
        }
        return new File(dir, name);
    }

    public String[] getFiles() {
        String[] names = dir.list(new FilenameFilter() {

            @Override
            public boolean accept(File file, String string) {
                return string.endsWith(suffix.getSuffix());
            }
        });
        String[] result = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            result[i] = suffix.remove(names[i]);
        }
        return result;
    }

    public File createFile(String name) {
        File target = getFile(name);
        try {
            if (target.createNewFile()) {
                return target;
            } else {
                return null;
            }
        } catch (IOException ioe) {
            LOGGER.warn("Unable to create file `" + target.toString() + "'.", ioe);
            return null;
        }
    }

    public void deleteFile(String name) {
        File target = getFile(name);
        if (!target.delete()) {
            target.deleteOnExit();
            LOGGER.error("Unable to delete `" + target.toString() + "'. Will try removing it on program exit.");
        }
    }
}
