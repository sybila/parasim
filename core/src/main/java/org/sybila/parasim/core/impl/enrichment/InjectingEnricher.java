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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.sybila.parasim.core.annotation.Inject;
import org.sybila.parasim.core.api.Context;
import org.sybila.parasim.core.api.InjectionPoint;
import org.sybila.parasim.core.common.ReflectionUtils;
import org.sybila.parasim.core.impl.GenericInjectionPoint;
import org.sybila.parasim.core.spi.enrichment.Enricher;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InjectingEnricher implements Enricher<Object> {

    @Override
    public void enrich(Object target, Context context) {
        if (target == null) {
            throw new IllegalArgumentException("The parameter [target] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        for (Field field : ReflectionUtils.getDeclaredFieldsRecursively(target.getClass())) {
            if (field.getAnnotation(Inject.class) != null) {
                InjectionPoint injectionPoint = new GenericInjectionPoint(field.getAnnotations(), field.getGenericType());
                Object value = context.resolve(ReflectionUtils.getType(injectionPoint.getType()), injectionPoint.getQualifier());
                if (value == null) {
                    if (injectionPoint.required()) {
                        throw new IllegalStateException("The instance of " + field.getType().getName() + " (" + injectionPoint.getQualifier().getSimpleName() + ") requested in " + target.getClass().getName() + " is not available, context [" + context.getScope().getSimpleName() + "]");
                    } else {
                        continue;
                    }
                }
                field.setAccessible(true);
                try {
                    field.set(target, field.getType().cast(value));
                } catch (Exception ex) {
                    throw new IllegalStateException("The instace can't be injected.", ex);
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
            InjectionPoint injectionPoint = new GenericInjectionPoint(method.getParameterAnnotations()[i], method.getGenericParameterTypes()[i]);
            args[i] = context.resolve(ReflectionUtils.getType(injectionPoint.getType()), injectionPoint.getQualifier());
            if (args[i] == null) {
                if (injectionPoint.required()) {
                    throw new IllegalStateException("The instance of " + method.getParameterTypes()[i] + " requested in " + method.getDeclaringClass().getName() + "#" + method.getName() + "() is not available, context [" + context.getScope().getSimpleName() + "]");
                }
            }
        }
    }

    @Override
    public int getPrecedence() {
        return 10;
    }

    @Override
    public Class<Object> getType() {
        return Object.class;
    }
}
