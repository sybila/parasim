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
package org.sybila.parasim.core.extension.loader.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.InvocationException;
import org.sybila.parasim.core.extension.loader.api.ExtensionLoader;
import org.sybila.parasim.core.LoadableExtension;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SPIExtensionLoader implements ExtensionLoader {

    private static final String SERVICES = "META-INF" + File.separator + "services";
    private static final Logger LOGGER = LoggerFactory.getLogger(SPIExtensionLoader.class);

    public Collection<LoadableExtension> load() {
        Collection<LoadableExtension> extensions = new ArrayList<LoadableExtension>();
        for (Class<? extends LoadableExtension> loadableClass: load(SPIExtensionLoader.class.getClassLoader(), LoadableExtension.class)) {
            try {
                extensions.add(createInstance(loadableClass));
            } catch(Exception e) {
                LOGGER.warn("Error while loading extension " + loadableClass.getName());
            }
        }
        return extensions;
    }

    private <T> Collection<Class<? extends T>> load(ClassLoader classLoader, Class<T> serviceClass) {
        String path = SERVICES + File.separator + serviceClass.getName();
        Collection<Class<? extends T>> services = new HashSet<Class<? extends T>>();
        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                BufferedReader reader = null;
                String className = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(resource.openStream(), "UTF-8"));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        className = line.split("#")[0];
                        services.add(classLoader.loadClass(className).asSubclass(serviceClass));
                    }
                } catch(Exception e) {
                    LOGGER.warn("Error while loading service " + className + ".", e);
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
        } catch(Exception e) {
            LOGGER.warn("Error while loading service.", e);
        }
        return services;
    }

    private <T> T createInstance(Class<T> type) throws Exception {
        for (Constructor<?> constructor: type.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
               if (!constructor.isAccessible()) {
                   constructor.setAccessible(true);
               }
               return (T) constructor.newInstance();
            }
        }
        throw new InvocationException("There is no empty constructor in class " + type.getName());
    }
}