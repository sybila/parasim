package org.sybila.parasim.extension.projectmanager.names;

import java.util.Collection;
import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
 */
public enum ProjectNamesFactory implements XMLRepresentableFactory<ProjectNames> {

    INSTANCE;
    static final String PROJECT_NAME = "project";
    static final String ODE_MODEL_NAME = "model";
    static final String FORMULAE_NAME = "formulae";
    static final String INIT_SPACES_NAME = "initial-spaces";
    static final String SIM_SPACES_NAME = "simulation-spaces";
    static final String PRECISIONS_NAME = "precision-configurations";
    static final String SAMPLINGS_NAME = "initial-samplings";
    static final String EXPERIMENTS_NAME = "experiments";
    static final String ACTIVE_NAME = "active-experiment";
    static final String ITEM_NAME = "item";

    private static void fill(Collection<String> names, Node parent) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals(ITEM_NAME)) {
                names.add(child.getFirstChild().getNodeValue());
            }
        }
    }

    @Override
    public ProjectNames getObject(Node source) throws XMLFormatException {
        if (!source.getNodeName().equals(PROJECT_NAME)) {
            throw new XMLFormatException("Wrong name of element: " + source.getNodeName() + " (expected " + PROJECT_NAME + ")");
        }
        ProjectNames result = new ProjectNames();
        NodeList children = source.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            switch (child.getNodeName()) {
                case ODE_MODEL_NAME:
                    result.setModelName(child.getFirstChild().getNodeValue());
                    break;
                case FORMULAE_NAME:
                    fill(result.getFormulaeNames(), child);
                    break;
                case INIT_SPACES_NAME:
                    fill(result.getInitialSpacesNames(), child);
                    break;
                case SIM_SPACES_NAME:
                    fill(result.getSimulationSpacesNames(), child);
                    break;
                case PRECISIONS_NAME:
                    fill(result.getPrecisionConfigurationsNames(), child);
                    break;
                case SAMPLINGS_NAME:
                    fill(result.getInitialSamplingsNames(), child);
                    break;
                case EXPERIMENTS_NAME:
                    fill(result.getExperimentsNames(), child);
                    break;
                case ACTIVE_NAME:
                    result.setActiveExperiment(child.getFirstChild().getNodeValue());
                    break;
                default:
                    throw new XMLFormatException("Unknown " + PROJECT_NAME + " item: " + child.getNodeName());
            }
        }
        return result;
    }

    public static XMLRepresentableFactory<ProjectNames> getInstance() {
        return INSTANCE;
    }
}
