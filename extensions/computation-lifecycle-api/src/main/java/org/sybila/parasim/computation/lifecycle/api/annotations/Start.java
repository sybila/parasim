package org.sybila.parasim.computation.lifecycle.api.annotations;

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
public @interface Start {

    public static final int NUMBER_OF_THREADS_AUTO = 0;

    /**
     * If it's set to false, the container changes status when the method calling starts
     * and finishes.
     */
    boolean controlsLifeCycle() default false;

    /**
     * Defines number of threads used to execute the annotated method. It's used
     * only when {@link Start#ownThread()} is set to true.
     *
     * If it is set to 0, the number of threads is determined according
     * to {@link Runtime#availableProcessors() }
     */
    int numberOfThreads() default 1;

    /**
     * If it's set to false, the container executes the method in its own thread.
     */
    boolean ownThread() default false;
}