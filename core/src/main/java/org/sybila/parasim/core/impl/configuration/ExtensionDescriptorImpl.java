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
package org.sybila.parasim.core.impl.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptor;
import org.sybila.parasim.core.api.configuration.Property;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExtensionDescriptorImpl implements ExtensionDescriptor {

    private String name;
    private Map<String, Property> properties = new HashMap<>();
    private String propertyNamePrefix;

    public ExtensionDescriptorImpl(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The parameter [name] is null.");
        }
        this.name = name;
        this.propertyNamePrefix = "parasim." + camelCaseToPropertyName(name) + ".";
    }

    @Override
    public boolean containsProperty(String name) {
        String propertyName = propertyNamePrefix + camelCaseToPropertyName(name);
        return System.getProperty(propertyName) != null || properties.containsKey(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getProperty(String name) {
        return getProperty(name, null);
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        String propertyName = propertyNamePrefix + camelCaseToPropertyName(name);
        Property prop = properties.get(name);
        if (prop == null) {
            return System.getProperty(propertyName, defaultValue);
        }
        if (!(prop.getValue() instanceof String)) {
            throw new IllegalStateException("The value of the property is not String.");
        }
        return System.getProperty(propertyName, (String) prop.getValue());
    }

    @Override
    public String[] getPropertyAsArray(String name) {
        return getPropertyAsArray(name, null);
    }

    @Override
    public String[] getPropertyAsArray(String name, String[] defaultValue) {
        Property prop = properties.get(name);
        if (prop.getValue().getClass() != String[].class) {
            throw new IllegalStateException("The value of the property is not String array.");
        }
        return prop == null ? defaultValue : (String[]) prop.getValue();
    }

    @Override
    public boolean isPropertyArray(String name) {
        Property prop = properties.get(name);
        return prop != null && prop.getValue().getClass().isArray();
    }

    @Override
    public ExtensionDescriptor setProperty(String name, String value) {
        properties.put(name, Property.of(name, value));
        return this;
    }

    @Override
    public ExtensionDescriptor setProperty(String name, String[] value) {
        properties.put(name, Property.of(name, value));
        return this;
    }

    @Override
    public Iterator<Property> iterator() {
        return properties.values().iterator();
    }

    private static String camelCaseToPropertyName(String value) {
        return value.replaceAll(
              String.format("%s|%s|%s",
                 "(?<=[A-Z])(?=[A-Z][a-z])",
                 "(?<=[^A-Z])(?=[A-Z])",
                 "(?<=[A-Za-z])(?=[^A-Za-z])"
              ),
              "."
           ).replace("-", "").toLowerCase();
    }
}
