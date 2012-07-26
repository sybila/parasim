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
package org.sybila.parasim.core.extension.lifecycle;

import java.lang.annotation.Annotation;
import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.lifecycle.spi.Constructor;
import org.sybila.parasim.core.extension.lifecycle.spi.Destructor;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestedLifecycleExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(Constructor.class, IntegerConstructor.class);
        builder.service(Destructor.class, IntegerDestructor.class);
    }

    private static class IntegerConstructor implements Constructor<Integer, Object> {

        @Override
        public Integer create(Object configuration, Class<? extends Annotation> qualifier) {
            return 1;
        }

        @Override
        public int getPrecedence() {
            return 0;
        }

    }

    private static class IntegerDestructor implements Destructor<Integer> {

        @Override
        public void destroy(Integer instance) {
            TestLifecycleExtension.destroyedCounter++;
        }

        @Override
        public int getPrecedence() {
            return 0;
        }

    }

}
