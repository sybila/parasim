package org.sybila.parasim.extension.projectManager.names;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.sybila.parasim.extension.projectManager.project.Project;
import org.sybila.parasim.extension.projectManager.project.ResourceList;
import org.sybila.parasim.model.xml.XMLRepresentable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
 */
public class ProjectNames implements XMLRepresentable {

    private String odeName, active;
    private Set<String> formulae = new HashSet<>();
    private Set<String> initSpaces = new HashSet<>();
    private Set<String> simSpaces = new HashSet<>();
    private Set<String> precisions = new HashSet<>();
    private Set<String> samplings = new HashSet<>();
    private Set<String> experiments = new HashSet<>();

    private static void addAll(Collection<String> target, ResourceList<?> src) {
        for (String item : src.getNames()) {
            target.add(item);
        }
    }

    public static ProjectNames getFromProject(Project target) {
        ProjectNames res = new ProjectNames();
        res.setModelName(target.getModelName());
        addAll(res.formulae, target.getFormulae());
        addAll(res.initSpaces, target.getInitialSpaces());
        addAll(res.simSpaces, target.getSimulationSpaces());
        addAll(res.precisions, target.getPrecisionConfigurations());
        addAll(res.samplings, target.getInitialSamplings());
        return res;
    }

    public String getModelName() {
        return odeName;
    }

    public void setModelName(String newName) {
        odeName = newName;
    }

    public Set<String> getFormulaeNames() {
        return formulae;
    }

    public Set<String> getInitialSpacesNames() {
        return initSpaces;
    }

    public Set<String> getSimulationSpacesNames() {
        return simSpaces;
    }

    public Set<String> getPrecisionConfigurationsNames() {
        return precisions;
    }

    public Set<String> getInitialSamplingsNames() {
        return samplings;
    }

    public Set<String> getExperimentsNames() {
        return experiments;
    }

    public String getActiveExperiment() {
        return active;
    }

    public void setActiveExperiment(String name) {
        active = name;
    }

    private static Element toXML(Document doc, Set<String> target, String name) {
        Element result = doc.createElement(name);
        for (String item : target) {
            Element itemElem = doc.createElement(ProjectNamesFactory.ITEM_NAME);
            itemElem.appendChild(doc.createTextNode(item));
            result.appendChild(itemElem);
        }
        return result;
    }

    @Override
    public Element toXML(Document doc) {
        Element result = doc.createElement(ProjectNamesFactory.PROJECT_NAME);

        Element ode = doc.createElement(ProjectNamesFactory.ODE_MODEL_NAME);
        ode.appendChild(doc.createTextNode(odeName));
        result.appendChild(ode);

        result.appendChild(toXML(doc, formulae, ProjectNamesFactory.FORMULAE_NAME));
        result.appendChild(toXML(doc, initSpaces, ProjectNamesFactory.INIT_SPACES_NAME));
        result.appendChild(toXML(doc, simSpaces, ProjectNamesFactory.SIM_SPACES_NAME));
        result.appendChild(toXML(doc, precisions, ProjectNamesFactory.PRECISIONS_NAME));
        result.appendChild(toXML(doc, samplings, ProjectNamesFactory.SAMPLINGS_NAME));
        result.appendChild(toXML(doc, experiments, ProjectNamesFactory.EXPERIMENTS_NAME));

        Element act = doc.createElement(ProjectNamesFactory.ACTIVE_NAME);
        act.appendChild(doc.createTextNode(getActiveExperiment()));
        result.appendChild(act);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ProjectNames)) {
            return false;
        }
        ProjectNames target = (ProjectNames) obj;
        if (!getModelName().equals(target.getModelName())) {
            return false;
        }
        if (!getFormulaeNames().equals(target.getFormulaeNames())) {
            return false;
        }
        if (!getInitialSpacesNames().equals(target.getInitialSpacesNames())) {
            return false;
        }
        if (!getSimulationSpacesNames().equals(target.getSimulationSpacesNames())) {
            return false;
        }
        if (!getPrecisionConfigurationsNames().equals(target.getPrecisionConfigurationsNames())) {
            return false;
        }
        if (!getInitialSamplingsNames().equals(target.getInitialSamplingsNames())) {
            return false;
        }
        if (!getExperimentsNames().equals(target.getExperimentsNames())) {
            return false;
        }
        if (!getActiveExperiment().equals(target.getActiveExperiment())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 53;
        int result = (getModelName() != null) ? getModelName().hashCode() : 0;
        result = prime * result + getFormulaeNames().hashCode();
        result = prime * result + getInitialSpacesNames().hashCode();
        result = prime * result + getSimulationSpacesNames().hashCode();
        result = prime * result + getPrecisionConfigurationsNames().hashCode();
        result = prime * result + getInitialSamplingsNames().hashCode();
        result = prime * result + getExperimentsNames().hashCode();
        result = prime * result + getActiveExperiment().hashCode();
        return result;
    }
}
