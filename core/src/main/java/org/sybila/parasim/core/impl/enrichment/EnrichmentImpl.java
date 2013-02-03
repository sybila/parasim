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
package org.sybila.parasim.core.impl.enrichment;

import java.lang.reflect.Method;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.ServiceRepository;
import org.sybila.parasim.core.api.enrichment.Enrichment;
import org.sybila.parasim.core.spi.enrichment.Enricher;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class EnrichmentImpl implements Enrichment {

    private final ServiceRepository serviceRepository;

    public EnrichmentImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    public void enrich(Object target, Context context) {
        for (Enricher enricher: serviceRepository.service(Enricher.class)) {
            if (enricher.getType().isAssignableFrom(target.getClass())) {
                enricher.enrich(target, context);
            }
        }
    }

    @Override
    public Object[] resolve(Method method, Context context) {
        Object[] args = new Object[method.getParameterTypes().length];
        for (Enricher enricher: serviceRepository.service(Enricher.class)) {
            if (enricher.getType().isAssignableFrom(method.getDeclaringClass())) {
                enricher.resolve(method, args, context);
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
