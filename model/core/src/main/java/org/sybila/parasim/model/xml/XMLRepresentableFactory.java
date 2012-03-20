package org.sybila.parasim.model.xml;

import org.w3c.dom.Node;


/**
 * Transforms XML elements into objects.
 * 
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 * @param <T> Type of objects which will be created.
 */
public interface XMLRepresentableFactory<T extends XMLRepresentable> {

    /**
     * Creates a new object from XML.
     * @param source XML element to be transformed.
     * @return New object created from <code>source</code>.
     * @throws XMLFormatException when input could not be parsed correctly.
     */
    public T getObject(Node source) throws XMLFormatException;
}
