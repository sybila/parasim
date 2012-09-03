/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.execution.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.enrichment.api.Enrichment;
import org.sybila.parasim.execution.api.ComputationContext;
import org.sybila.parasim.execution.api.ComputationInstanceContext;
import org.sybila.parasim.execution.api.Executor;
import org.sybila.parasim.execution.conf.ExecutionConfiguration;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractExecutor implements Executor {

    private final ContextEvent<ComputationContext> computationContextEvent;
    private final ContextEvent<ComputationInstanceContext> computationInstanceContextEvent;
    private final Enrichment enrichment;
    private final ExecutionConfiguration configuration;

    public AbstractExecutor(final ContextEvent<ComputationContext> computationContextEvent, final ContextEvent<ComputationInstanceContext> computationInstanceContextEvent, final Enrichment enrichment, final ExecutionConfiguration configuration) {
        Validate.notNull(computationContextEvent);
        Validate.notNull(computationInstanceContextEvent);
        Validate.notNull(enrichment);
        Validate.notNull(configuration);
        this.computationContextEvent = computationContextEvent;
        this.computationInstanceContextEvent = computationInstanceContextEvent;
        this.enrichment = enrichment;
        this.configuration = configuration;
    }

    protected final ContextEvent<ComputationContext> getComputationContextEvent() {
        return computationContextEvent;
    }

    protected final ContextEvent<ComputationInstanceContext> getComputationInstanceContextEvent() {
        return computationInstanceContextEvent;
    }

    protected final Enrichment getEnrichment() {
        return enrichment;
    }

    protected final ExecutionConfiguration getConfiguration() {
        return configuration;
    }

    protected void executeMethodsByAnnotation(final Enrichment enrichment, final Context context, final Object target, final Class<? extends Annotation> annotation) {
        for (Method method : target.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(annotation) != null) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                try {
                    method.invoke(target, enrichment.resolve(method, context));
                } catch (Exception ex) {
                    throw new IllegalStateException("Can't invoke " + target.getClass() + "#" + method.getName() + "()");
                }
            }
        }
    }

}
