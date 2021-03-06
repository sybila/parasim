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
package org.sybila.parasim.model.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Uses files to store/load objects. Is associated with file.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 * @param <T> Type of object to be stored/loaded.
 */
public abstract class FileXMLResource<T extends XMLRepresentable> extends StreamXMLResource<T> {
    private File target;

    /**
     * Sets file used to store/load objects.
     * @param file File used to store/load objects.
     */
    public FileXMLResource(File file) {
        if (file == null) {
            throw new IllegalArgumentException("The parameter [file] is null.");
        }
        target = file;
    }

    /**
     * @return File used to store/load objects.
     */
    public File getTargetFile() {
        return target;
    }

    @Override
    protected InputStream openInputStream() throws XMLException {
        try {
            return new FileInputStream(target);
        } catch (FileNotFoundException fnfe) {
            throw new XMLException("File not found", fnfe);
        }
    }

    @Override
    protected OutputStream openOutputStream() throws XMLException {
        try {
            return new FileOutputStream(target);
        } catch (FileNotFoundException fnfe) {
            throw new XMLException("File not found", fnfe);
        }
    }
}