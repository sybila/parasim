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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.enrichment.EnrichmentRegistrar;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InjectingEnricher extends AbstractEnricher<Object> {

    @Override
    public void enrich(Object target, Context context) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        for (Field field : target.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                Object service = null;
                Class<? extends Annotation> qualifier = loadQualifier(field.getDeclaredAnnotations());
                if (qualifier != null) {
                    service = getManager().resolve(field.getType(), qualifier, context);
                } else {
                    service = getManager().resolve(field.getType(), Default.class, context);
                }
                if (service == null) {
                    throw new IllegalStateException("The service " + field.getType().getName() + " requested in " + target.getClass().getName() + " is not available, context [" + context + "]");
                }
                field.setAccessible(true);
                try {
                    field.set(target, field.getType().cast(service));
                } catch (Exception ex) {
                    throw new IllegalStateException("The service can't be injected.", ex);
                }
            }
        }
    }

    @Override
    public void resolve(Method method, Object[] args, Context context) {
        for (int i=0; i<args.length; i++) {
            if (args[i] != null) {
                continue;
            }
            Class<? extends Annotation> qualifier = loadQualifier(method.getParameterAnnotations()[i]);
            if (qualifier == null) {
                args[i] = getManager().resolve(method.getParameterTypes()[i], Default.class, context);
            } else {
                args[i] = getManager().resolve(method.getParameterTypes()[i], qualifier, context);
            }
            if (args[i] == null) {
                throw new IllegalStateException("The service " + method.getParameterTypes()[i] + " requested in " + method.getDeclaringClass().getName() + "#" + method.getName() + "() is not available, context [" + context + "]");
            }
        }
    }

    private Manager getManager() {
        return EnrichmentRegistrar.getManager();
    }

}
