package org.sybila.parasim.extension.projectmanager.model.project;

import java.util.Set;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ResourceList<T> {

    public Set<String> getNames();

    public boolean add(String name, T target);

    public T get(String name);

    public void remove(String name);

    public boolean rename(String name, String newName);
}