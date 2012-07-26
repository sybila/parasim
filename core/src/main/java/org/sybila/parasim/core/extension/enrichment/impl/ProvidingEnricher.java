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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.ProviderImpl;
import org.sybila.parasim.core.ProvidingFieldPoint;
import org.sybila.parasim.core.ProvidingMethodPoint;
import org.sybila.parasim.core.ProvidingPoint;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.enrichment.EnrichmentRegistrar;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ProvidingEnricher extends AbstractEnricher<Object> {

    @Override
    public void enrich(Object target, Context context) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        for (Field field: target.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Provide.class) != null) {
                ProvidingPoint providingPoint = new ProvidingFieldPoint(target, field);
                Class<?> type = getType(providingPoint.getType());
                ProviderImpl.bind(getManager(), context, providingPoint, type, providingPoint.getQualifier());
            }
        }
        for (Method method: target.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(Provide.class) != null) {
                ProvidingPoint providingPoint = new ProvidingMethodPoint(target, method, context, method.getAnnotation(Provide.class).fresh());
                Class<?> type = getType(providingPoint.getType());
                ProviderImpl.bind(getManager(), context, providingPoint, type, providingPoint.getQualifier());
            }
        }
    }

    @Override
    public void resolve(Method method, Object[] args, Context context) {
    }

    private ManagerImpl getManager() {
        return EnrichmentRegistrar.getManager();
    }
}
