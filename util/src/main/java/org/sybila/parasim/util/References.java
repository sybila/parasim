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
