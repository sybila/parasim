package org.sybila.parasim.computation.lifecycle.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated by this annotation will be executed by
 * {@link DefaultComputationContainer#finalize(org.sybila.parasim.computation.lifecycle.Computation computation)}
 * method. Annotated methods shouldn't create new threads.
 * 
 * After methods are called, the computation status is set to finalized.
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface  After {}
