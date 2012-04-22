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
package org.sybila.parasim.core.extension.configuration.impl;

import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ParasimDescriptorImpl implements ParasimDescriptor {

    private static DocumentBuilder builder;
    private Map<String, ExtensionDescriptor> extensions = new HashMap<String, ExtensionDescriptor>();

    private ParasimDescriptorImpl() {}

    public ExtensionDescriptor getExtensionDescriptor(String name) {
        if (!extensions.containsKey(name)) {
            extensions.put(name, new ExtensionDescriptorImpl(name));
        }
        return extensions.get(name);
    }

    public Collection<ExtensionDescriptor> getExtensionDescriptors() {
        return Collections.unmodifiableCollection(extensions.values());
    }

    public static ParasimDescriptor fromXMLFile(String propertyName, String defaultPath) throws IOException, SAXException {
        File configFile = loadFileDescriptor(propertyName, defaultPath);
        Document dom = getBuilder().parse(configFile);
        NodeList extensions = dom.getElementsByTagName("extension");
        ParasimDescriptorImpl descriptor = new ParasimDescriptorImpl();
        for (int i=0; i<extensions.getLength(); i++) {
            String qualifier = extensions.item(i).getAttributes().getNamedItem("qualifier").getTextContent();
            if (qualifier == null) {
                throw new IOException("There is an extension without qualifier.");
            }
            ExtensionDescriptor extensionDescriptor = new ExtensionDescriptorImpl(qualifier);
            for (int j=0; j < extensions.item(i).getChildNodes().getLength(); j++) {
                Node property = extensions.item(i).getChildNodes().item(j);
                if (!property.getNodeName().equals("property")) {
                    continue;
                }
                String name = property.getAttributes().getNamedItem("name").getTextContent();
                if (name == null) {
                    throw new IOException("There is a property without name in the configuration of [" + qualifier + "] extension.");
                }
                if (isArray(property)) {
                    // property is array
                    List<String> values = new ArrayList<String>();
                    for (int k=0; k<property.getChildNodes().getLength(); k++) {
                        Node value = property.getChildNodes().item(k);
                        if (!value.getNodeName().equals("value")) {
                            continue;
                        }
                        values.add(value.getTextContent());
                    }
                    String[] valuesInArray = new String[values.size()];
                    values.toArray(valuesInArray);
                    extensionDescriptor.setProperty(name, valuesInArray);
                } else {
                    // property isn't array
                    String value = property.getTextContent().trim();
                    extensionDescriptor.setProperty(name, value);
                }
            }
            descriptor.extensions.put(qualifier, extensionDescriptor);
        }
        return descriptor;
    }

    private static DocumentBuilder getBuilder() {
        if (builder == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                builder = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return builder;
    }

    private static boolean isArray(Node node) {
        for (int i=0; i<node.getChildNodes().getLength(); i++) {
            if (node.getChildNodes().item(i).getNodeName().equals("value")) {
                return true;
            }
        }
        return false;
    }

    private static File loadFileDescriptor(String propertyName, String defaultPath) {
        String path = System.getProperty(propertyName);
        if (path == null) {
            path = defaultPath;
        }
        return new File(path);
    }

    public boolean isVerbose() {
        if (System.getProperty("parasim.verbose") != null) {
            return true;
        } else {
            ExtensionDescriptor coreExtensionDescriptor = getExtensionDescriptor("core");
            return coreExtensionDescriptor != null && coreExtensionDescriptor.containsProperty("parasim.verbose") && Boolean.getBoolean(coreExtensionDescriptor.getProperty("verbose"));
        }
    }

}
