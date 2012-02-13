package org.sybila.parasim.computation.lifecycle.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated by this annotation will be executed by
 * {@link DefaultComputationContainer#start(org.sybila.parasim.computation.lifecycle.Computation computation)}
 * method. Annotated methods shouldn't create new threads.
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface  Start {
    /**
     * If it's set to false, the container executes the method in its own thread.
     */
    boolean ownThread() default false;
    
    /**
     * If it's set to false, the container changes status when the method calling starts
     * and finishes.
     */
    boolean controlsLifeCycle() default false;
}
