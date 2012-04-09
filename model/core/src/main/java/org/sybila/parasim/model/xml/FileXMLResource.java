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