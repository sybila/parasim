package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.api.InitialSamplingResource;
import org.sybila.parasim.extension.projectmanager.model.projectimpl.DirProject.ExperimentAction;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.model.xml.XMLResource;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class InitialSamplingResourceList extends XMLResourceList<InitialSampling> {

    public InitialSamplingResourceList(DirProject parent) {
        super(parent, ExperimentSuffixes.INITIAL_SAMPLING);
    }

    @Override
    protected ExperimentAction getAction(final String name, final String newName) {
        return new ExperimentAction() {

            @Override
            public void apply(ExperimentNames target) {
                if (target.getInitialSamplingName().equals(name)) {
                    target.setInitialSamplingName(newName);
                }
            }
        };
    }

    @Override
    protected XMLResource<InitialSampling> getXMLResource(File target) {
        return new InitialSamplingResource(target, getParent().getOdeSystem());
    }
}
