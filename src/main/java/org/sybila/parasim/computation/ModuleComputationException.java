package org.sybila.parasim.computation;

/**
 * Thrown when a module computation crashes.
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ModuleComputationException extends Exception {

    /**
     * Constructs a <tt>ModuleComputationException</tt> with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * 
     * @param cause the cause (which is saved for later retrieval by the
     *      {@link Throwable#getCause()} method).  (A <tt>null</tt> value is
     *      permitted, and indicates that the cause is nonexistent or
     *      unknown.)
     */    
    public ModuleComputationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <tt>ModuleComputationException</tt> with the specified detail message and
     * cause.
     *
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     * 
     * @param message the String that contains a detailed message
     * @param cause the cause (which is saved for later retrieval by the
     *      {@link Throwable#getCause()} method).  (A <tt>null</tt> value
     *      is permitted, and indicates that the cause is nonexistent or
     *      unknown.)
     */    
    public ModuleComputationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a <tt>ModuleComputationException</tt> with the specified detail message.
     * A detail message is a String that describes this particular exception. 
     * 
     * @param message the String that contains a detailed message
     */    
    public ModuleComputationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a <tt>ModuleComputationException</tt> with no detail message.
     */
    public ModuleComputationException() {
    }
}
