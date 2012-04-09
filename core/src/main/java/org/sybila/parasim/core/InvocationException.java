package org.sybila.parasim.core;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InvocationException extends RuntimeException {

    public InvocationException(Throwable cause) {
        super(cause);
    }

    public InvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvocationException(String message) {
        super(message);
    }

    public InvocationException() {
    }

}
