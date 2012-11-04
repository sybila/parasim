package org.sybila.parasim.extension.projectManager.model.project;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ResourceList<T> {

    public boolean add(String name, T target);

    public T get(String name);

    public void put(String name, T target);

    public boolean isUsedInExperiment(String name);

    public void remove(String name);

    public boolean rename(String name, String newName);

    public boolean duplicate(String name, String newName);
}
