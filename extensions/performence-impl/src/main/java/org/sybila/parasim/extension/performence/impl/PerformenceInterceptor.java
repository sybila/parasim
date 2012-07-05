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
package org.sybila.parasim.extension.performence.impl;

import java.lang.reflect.Method;
import org.sybila.parasim.core.extension.interceptor.api.Invocation;
import org.sybila.parasim.core.service.Interceptor;
import org.sybila.parasim.extension.performence.api.MethodMeasurement;
import org.sybila.parasim.extension.performence.api.annotation.Measurement;
import org.sybila.parasim.util.Debug;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class PerformenceInterceptor implements Interceptor {

    @Override
    public Object intercept(final Object obj, final Method method, Object[] args, Invocation invocation) throws Throwable {
        final Measurement measurement = method.getAnnotation(Measurement.class);
        if (measurement != null && !measurement.enabled()) {
            return invocation.invoke(obj, method, args);
        }
        Debug.time();
        Debug.memory();
        Object result = invocation.invoke(obj, method, args);
        final long memory = Debug.memory();
        final long time = Debug.time();
        MeasurementDatabase.process(new MethodMeasurement() {

            @Override
            public long getUsedMemory() {
                return memory;
            }

            @Override
            public long getTime() {
                return time;
            }

            @Override
            public Method getMethod() {
                return method;
            }
        });
        return result;
    }

}
