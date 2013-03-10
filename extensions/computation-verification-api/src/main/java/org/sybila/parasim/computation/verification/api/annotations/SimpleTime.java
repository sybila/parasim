package org.sybila.parasim.computation.verification.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.sybila.parasim.core.annotation.Qualifier;

/**
 * Indicates that normal STL semantics should be used. Freeze operators and
 * frozen-time dependent terms of predicates (when present) are ignored.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
@Qualifier
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SimpleTime {
}
