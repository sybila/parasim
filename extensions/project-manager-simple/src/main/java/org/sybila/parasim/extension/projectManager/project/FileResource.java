package org.sybila.parasim.extension.projectManager.project;

import java.io.File;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface FileResource<T> {

    public T getRoot();

    public void setRoot(T target);

    public File getFile();

    public void store() throws ResourceException;

    public void load() throws ResourceException;

    public static interface Factory<T, R extends FileResource<T>> {

        public R get(File target);
    }
}
