package org.sybila.parasim.extension.projectManager.view;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ValueHolder<T> {

    public T getValues();

    public void setValues(T target);
}
