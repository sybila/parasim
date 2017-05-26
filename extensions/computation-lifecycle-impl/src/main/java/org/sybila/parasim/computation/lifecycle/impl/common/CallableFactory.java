/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.computation.lifecycle.impl.common;

import java.util.UUID;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.MutableStatus;
import org.sybila.parasim.computation.lifecycle.api.annotations.ComputationInstanceScope;
import org.sybila.parasim.computation.lifecycle.api.annotations.Node;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.model.Mergeable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class CallableFactory {

    private final Context parentContext;
    private final MutableStatus status;

    private static final Logger LOGGER = LoggerFactory.getLogger(CallableFactory.class);

    public CallableFactory(Context parentContext, MutableStatus status) {
        this.parentContext = parentContext;
        this.status = status;
    }

    public <M extends Mergeable<M>> Callable<M> instance(Computation<M> computation) {
        return new ComputationInstanceCallable<>(computation, parentContext, status);
    }

    private static class ComputationInstanceCallable<M extends Mergeable<M>> implements Callable<M> {

        private final Computation<M> computation;
        private final Context parentContext;
        private final MutableStatus status;

        public ComputationInstanceCallable(Computation<M> computation, Context parentContext, MutableStatus status) {
            Validate.notNull(computation);
            Validate.notNull(parentContext);
            Validate.notNull(status);
            this.computation = computation;
            this.parentContext = parentContext;
            this.status = status;
        }

        @Override
        public M call() throws Exception {
            Context instanceContext = parentContext.context(ComputationInstanceScope.class);
            try {
                Enrichment enrichment = instanceContext.resolve(Enrichment.class, Default.class);
                enrichment.enrich(computation, instanceContext);
                M result = computation.call();
                status.done(parentContext.resolve(UUID.class, Node.class), result);
                return result;
            } catch(Exception e) {
                LOGGER.error("There is an exception during computation execution.", e);
                throw new IllegalStateException("There is an exception during computation execution.", e);
            } finally {
                computation.destroy();
                instanceContext.destroy();
            }
        }
    }
}
