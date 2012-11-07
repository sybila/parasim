package org.sybila.parasim.extension.projectmanager.oldoroject;

import java.io.File;
import org.sybila.parasim.extension.projectmanager.project.ResourceException;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
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
