package org.sybila.parasim.model.computation;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ComputationFailedException extends Exception {

    public ComputationFailedException() {
    }

    public ComputationFailedException(String message) {
        super(message);
    }

    public ComputationFailedException(Throwable cause) {
        super(cause);
    }

    public ComputationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
