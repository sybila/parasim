package org.sybila.parasim.model.cdi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class XMLServiceFactory extends MapCDIFactory {

    private static DocumentBuilder builder;

    private XMLServiceFactory() {}
    
    public static ServiceFactory fromFile(File file) throws IOException {
        Map<Class<?>, Class<?>> mapping = new HashMap<Class<?>, Class<?>>();
        Map<Class<?>, List<Object>> dependencies = new HashMap<Class<?>, List<Object>>();
        try {
            Document dom = getBuilder().parse(file);
            NodeList servicesList = dom.getElementsByTagName("service");
            if (servicesList.getLength() == 0) {
                return new XMLServiceFactory();
            }
            for (int j = 0; j < servicesList.getLength(); j++) {
                Class<?> interfaze = null;
                Class<?> implementation = null;
                List<Object> currentDependencies = new ArrayList<Object>();
                NodeList serviceConf = servicesList.item(j).getChildNodes();
                for (int k = 0; k < serviceConf.getLength(); k++) {
                    Node currentNode = serviceConf.item(k);
                    if (currentNode.getNodeName().equals("interface")) {
                        interfaze = Class.forName(currentNode.getTextContent().trim());
                    } else if (currentNode.getNodeName().equals("class")) {
                        implementation = Class.forName(currentNode.getTextContent().trim());
                    } else if (currentNode.getNodeName().equals("dependencies")) {
                        for (int i=0; i<currentNode.getChildNodes().getLength(); i++) {
                            Node dependency = currentNode.getChildNodes().item(i);
                            if (dependency.getNodeName().equals("string")) {
                                currentDependencies.add(dependency.getTextContent().trim());
                            } else if (dependency.getNodeName().equals("integer")) {
                                currentDependencies.add(Integer.parseInt(dependency.getTextContent().trim()));
                            } else if (dependency.getNodeName().equals("float")) {
                                currentDependencies.add(Float.parseFloat(dependency.getTextContent().trim()));
                            } else if (dependency.getNodeName().equals("boolean")) {
                                currentDependencies.add(Boolean.parseBoolean(dependency.getTextContent().trim()));
                            } 
                        }
                    }
                }
                if (interfaze == null) {
                    throw new IOException("The service name/interface is not given.");
                }
                if (implementation == null) {
                    throw new IOException("The service class is not given.");
                }
                mapping.put(interfaze, implementation);
                if (!currentDependencies.isEmpty()) {
                    dependencies.put(interfaze, currentDependencies);
                }
            }
        } catch (SAXException ex) {
            throw new IOException(ex);
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }
        XMLServiceFactory factory = new XMLServiceFactory();
        for (Class<?> interfaze : mapping.keySet()) {
            if (dependencies.containsKey(interfaze)) {
                factory.addService(interfaze, factory.createInstance(mapping.get(interfaze), dependencies.get(interfaze).toArray()));
            } else {
                factory.addService(interfaze, mapping.get(interfaze));
            }
        }
        return factory;
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
}
