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
package org.sybila.parasim.core.extension.enrichment.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.enrichment.api.Enrichment;
import org.sybila.parasim.core.extension.enrichment.spi.Enricher;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class EnrichmentImpl implements Enrichment {

    private Map<Class<?>, Collection<Enricher<?>>> enrichers = new HashMap<>();

    @Override
    public <T> void addEnricher(Class<T> type, Enricher<T> enricher) {
        Collection<Enricher<?>> enrichersByType = enrichers.get(type);
        if (enrichersByType == null) {
            enrichersByType = new ArrayList<>();
            enrichers.put(type, enrichersByType);
        }
        enrichersByType.add(enricher);
    }

    @Override
    public void enrich(Object target, Context context) {
        for (Entry<Class<?>, Collection<Enricher<?>>> entry: enrichers.entrySet()) {
            if (entry.getKey().isAssignableFrom(target.getClass())) {
                for (Enricher enricher: entry.getValue()) {
                    enricher.enrich(target, context);
                }
            }
        }
    }

    @Override
    public Object[] resolve(Method method, Context context) {
        Object[] args = new Object[method.getParameterTypes().length];
        for (Entry<Class<?>, Collection<Enricher<?>>> entry: enrichers.entrySet()) {
            if (entry.getKey().isAssignableFrom(method.getDeclaringClass())) {
                for (Enricher enricher: entry.getValue()) {
                    enricher.resolve(method, args, context);
                }
            }
        }
        for (int i=0; i<args.length; i++) {
            if (args[i] == null) {
                throw new IllegalStateException("The value for argument number [" + i + "] in method ["+method.getDeclaringClass() + "#" + method.getName() +"] is missing ");
            }
        }
        return args;
    }

}
