package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.PrecisionConfigurationResource;
import org.sybila.parasim.extension.projectmanager.model.projectimpl.DirProject.ExperimentAction;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.model.xml.XMLResource;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PrecisionConfigurationResourceList extends XMLResourceList<PrecisionConfiguration> {

    public PrecisionConfigurationResourceList(DirProject parent) {
        super(parent, ExperimentSuffixes.PRECISION_CONFIGURATION);
    }

    @Override
    protected ExperimentAction getAction(final String name, final String newName) {
        return new ExperimentAction() {

            @Override
            public void apply(ExperimentNames target) {
                if (target.getPrecisionConfigurationName().equals(name)) {
                    target.setPrecisionConfigurationName(newName);
                }
            }
        };
    }

    @Override
    protected XMLResource<PrecisionConfiguration> getXMLResource(File target) {
        return new PrecisionConfigurationResource(target);
    }
}
