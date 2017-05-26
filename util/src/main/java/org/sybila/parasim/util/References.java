/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class References {

    private References() {
    }

    public static Collection<Object> getTransitiveClosure(Object target, int depth) throws Exception {
        if (target == null) {
            return Collections.EMPTY_LIST;
        }
        Set<Object> references = new HashSet<>();
        Collection<Object> previousLevel = new HashSet<>();
        previousLevel.add(target);
        for (int i=0; i<depth; i++) {
            Collection<Object> currentLevel = new HashSet<>();
            for (Object o: previousLevel) {
                for (Field f: o.getClass().getDeclaredFields()) {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    Object fieldValue = f.get(o);
                    if (fieldValue != null) {
                        references.add(fieldValue);
                        if (!fieldValue.getClass().isPrimitive()) {
                            currentLevel.add(fieldValue);
                        }
                    }
                }
            }
            previousLevel = currentLevel;
        }
        return Collections.unmodifiableCollection(references);
    }

}
