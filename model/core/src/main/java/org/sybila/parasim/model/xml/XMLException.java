package org.sybila.parasim.model.xml;

/**
 * Exception during storing or loading of xml resource.
 *
 * @see XMLResource
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class XMLException extends Exception {

    public XMLException(String message) {
        super(message);
    }

    public XMLException(String message, Throwable cause) {
        super(message, cause);
    }
}
