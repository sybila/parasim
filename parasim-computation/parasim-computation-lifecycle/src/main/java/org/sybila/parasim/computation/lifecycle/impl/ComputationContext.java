package org.sybila.parasim.computation.lifecycle.impl;

import java.lang.annotation.Annotation;
import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationScope;
import org.sybila.parasim.core.context.AbstractContext;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ComputationContext extends AbstractContext {

    public Class<? extends Annotation> getScope() {
        return ComputationScope.class;
    }

}
