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

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.extension.interceptor.api.InterceptionException;
import org.sybila.parasim.core.extension.interceptor.api.InterceptorRegistry;
import org.sybila.parasim.extension.performence.api.Performence;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class PerformenceImpl implements Performence{

    private InterceptorRegistry interceptorRegistry;
    private PerformenceInterceptor interceptor = new PerformenceInterceptor();
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformenceImpl.class);

    public PerformenceImpl(InterceptorRegistry interceptorRegistry) {
        Validate.notNull(interceptorRegistry);
        this.interceptorRegistry = interceptorRegistry;
    }

    @Override
    public <T> T measure(T target) {
        try {
            return interceptorRegistry.intercepted(target).intercept(interceptor).getProxyObject();
        } catch (InterceptionException e) {
            LOGGER.warn("Can't measure <" + target + ">.", e);
            return target;
        }
    }

}
