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
package org.sybila.parasim.core.extension.enrichment;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.extension.enrichment.api.Enrichment;
import org.sybila.parasim.core.extension.enrichment.impl.EnrichmentImpl;
import org.sybila.parasim.core.extension.enrichment.spi.Enricher;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class EnrichmentRegistrar {

    @Inject
    private Instance<Enrichment> enrichmentInstance;

    private static ManagerImpl manager;

    public void registerEnrichment(@Observes ManagerStarted event, Manager manager) {
        EnrichmentRegistrar.manager = (ManagerImpl) manager;
        Enrichment enrichment = new EnrichmentImpl();
        for (Enricher enricher : manager.service(Enricher.class)) {
            Class<?> type = getFirstGenericParameterType(enricher.getClass(), Enricher.class);
            if (type != null) {
                enrichment.addEnricher(type, enricher);
            }
        }
        enrichmentInstance.set(enrichment);
    }

    private static Class<?> getFirstGenericParameterType(Class<?> clazz, Class<?> rawType) {
        for (Type interfaceType : clazz.getGenericInterfaces()) {
            if (interfaceType instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) interfaceType;
                if (rawType.isAssignableFrom((Class<?>) ptype.getRawType())) {
                    return (Class<?>) ptype.getActualTypeArguments()[0];
                }
            }
        }
        ParameterizedType ptype = (ParameterizedType) clazz.getGenericSuperclass();
        if (rawType.isAssignableFrom((Class<?>) ptype.getRawType())) {
            return (Class<?>) ptype.getActualTypeArguments()[0];
        }
        return null;
    }

    public static ManagerImpl getManager() {
        return manager;
    }

}
