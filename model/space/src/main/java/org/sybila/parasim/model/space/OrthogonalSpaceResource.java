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
package org.sybila.parasim.model.space;

import java.io.File;
import java.net.URL;

import org.sybila.parasim.model.xml.FileXMLResource;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;

public class OrthogonalSpaceResource extends FileXMLResource<OrthogonalSpace> {

    public OrthogonalSpaceResource(File file) {
        super(file);
    }

    @Override
    protected XMLRepresentableFactory<OrthogonalSpace> getFactory() {
        return new OrthogonalSpaceFactory();
    }

    @Override
    protected URL getXMLSchema() {
        return getClass().getClassLoader().getResource("space.xsd");
    }

    @Override
    protected String getNamespace() {
        return "http://www.sybila.org/parasim/space";
    }

}
