/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
public class SimulationSpaceResourceList extends XMLResourceList<OrthogonalSpace> {

    public SimulationSpaceResourceList(DirProject parent) {
        super(parent, ExperimentSuffixes.SIMULATION_SPACE);
    }

    @Override
    protected ExperimentAction getAction(final String name, final String newName) {
        return new ExperimentAction() {

            @Override
            public void apply(ExperimentNames target) {
                if (target.getSimulationSpaceName().equals(name)) {
                    target.setSimulationSpaceName(newName);
                }
            }
        };
    }

    @Override
    protected XMLResource<OrthogonalSpace> getXMLResource(File target) {
        return new OrthogonalSpaceResource(target, getParent().getOdeSystem());
    }
}
