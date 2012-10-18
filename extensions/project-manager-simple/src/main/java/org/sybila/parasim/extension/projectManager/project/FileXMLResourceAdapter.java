package org.sybila.parasim.extension.projectManager.project;

import java.io.File;
import org.sybila.parasim.model.xml.FileXMLResource;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.model.xml.XMLRepresentable;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FileXMLResourceAdapter<T extends XMLRepresentable> implements FileResource<T> {

    private FileXMLResource<T> resource;

    public FileXMLResourceAdapter(FileXMLResource<T> resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Argument (resource) is null.");
        }
        this.resource = resource;
    }

    @Override
    public File getFile() {
        return resource.getTargetFile();
    }

    @Override
    public T getRoot() {
        return resource.getRoot();
    }

    @Override
    public void load() throws ResourceException {
        try {
            resource.load();
        } catch (XMLException xmle) {
            throw new ResourceException("Unable to load from XML resource.", xmle);
        }
    }

    @Override
    public void setRoot(T target) {
        resource.setRoot(target);
    }

    @Override
    public void store() throws ResourceException {
        try {
            resource.store();
        } catch (XMLException xmle) {
            throw new ResourceException("Unable to store to XML resource.", xmle);
        }
    }

    public FileXMLResource<T> getResource() {
        return resource;
    }
}