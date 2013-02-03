/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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

import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.ContextFactory;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.execution.api.Execution;
import org.sybila.parasim.execution.api.SequentialExecutor;
import org.sybila.parasim.execution.api.annotations.ComputationScope;
import org.sybila.parasim.execution.conf.ExecutionConfiguration;
import org.sybila.parasim.model.Mergeable;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.computation.ComputationId;
import org.sybila.parasim.model.computation.annotations.Before;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SequentialExecutorImpl extends AbstractExecutor implements SequentialExecutor {

    private final java.util.concurrent.Executor runnableExecutor;

    public SequentialExecutorImpl(ContextFactory contextFactory, Enrichment enrichment, ExecutionConfiguration configuration, java.util.concurrent.Executor runnableExecutor) {
        super(contextFactory, enrichment, configuration);
        Validate.notNull(runnableExecutor);
        this.runnableExecutor = runnableExecutor;
    }

    @Override
    public <L extends Mergeable<L>> Execution<L> submit(Computation<L> computation) {
        Context context = getContextFactory().context(ComputationScope.class);
        executeMethodsByAnnotation(getEnrichment(), context, computation, Before.class);
        return new SequentialExecution<>(
                new ComputationId() {
                    @Override
                    public int currentId() {
                        return 0;
                    }

                    @Override
                    public int maxId() {
                        return 0;
                    }
                },
                runnableExecutor,
                computation,
                getEnrichment(),
                context
        );
    }
}