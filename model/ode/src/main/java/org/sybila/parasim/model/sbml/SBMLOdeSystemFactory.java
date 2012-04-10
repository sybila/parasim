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
package org.sybila.parasim.model.sbml;

import java.io.File;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLReader;
import org.sybila.parasim.model.ode.OdeSystem;

/**
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class SBMLOdeSystemFactory {

    public static OdeSystem fromFile(File file) throws IOException {
        Model model = modelFromFile(file);
        return new SBMLOdeSystem(model);
    }

    protected static Model modelFromFile(File file) throws IOException {
        try {
            return SBMLReader.read(file).getModel();
        } catch(XMLStreamException e) {
            throw new IOException(e);
        }
    }

}
