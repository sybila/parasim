package org.sybila.parasim.extension.projectmanager.project;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
 */
public interface ResourceList<E> {

    public boolean add(String name, E root);

    public boolean rename(String name, String newName);

    public void remove(String name);

    public void store(String name) throws ResourceException;

    public void load(String name) throws ResourceException;

    public E get(String name);

    public Iterable<String> getNames();

    public int size();

    public boolean isEmpty();
}
