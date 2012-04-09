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
                    String value = property.getTextContent();
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

}
