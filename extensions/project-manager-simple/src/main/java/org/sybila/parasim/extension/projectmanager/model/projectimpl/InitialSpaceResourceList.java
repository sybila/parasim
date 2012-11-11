package org.sybila.parasim.extension.projectmanager.model.projectimpl;

import java.io.File;
import org.sybila.parasim.extension.projectmanager.model.projectimpl.DirProject.ExperimentAction;
import org.sybila.parasim.extension.projectmanager.names.ExperimentNames;
import org.sybila.parasim.extension.projectmanager.names.ExperimentSuffixes;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.space.OrthogonalSpaceResource;
import org.sybila.parasim.model.xml.XMLResource;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class InitialSpaceResourceList extends XMLResourceList<OrthogonalSpace> {

    public InitialSpaceResourceList(DirProject parent) {
        super(parent, ExperimentSuffixes.INITIAL_SPACE);
    }

    @Override
    protected ExperimentAction getAction(final String name, final String newName) {
        return new ExperimentAction() {

            @Override
            public void apply(ExperimentNames target) {
                if (target.getInitialSpaceName().equals(name)) {
                    target.setInitialSpaceName(newName);
                }
            }
        };
    }

    @Override
    protected XMLResource<OrthogonalSpace> getXMLResource(File target) {
        return new OrthogonalSpaceResource(target, getParent().getOdeSystem());
    }
}
