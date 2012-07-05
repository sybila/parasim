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
package org.sybila.parasim.extension.performence.api;

import org.sybila.parasim.model.xml.XMLFormatException;
import org.sybila.parasim.model.xml.XMLRepresentableFactory;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MeasurementExportFactory implements XMLRepresentableFactory<MeasurementExport> {

    public static final String PERFORMENCE = "performence";
    public static final String CLASS = "class";
    public static final String METHOD = "method";
    public static final String AVERAGE_TIME = "avgTime";
    public static final String INVOCATIONS = "invocations";
    public static final String AVERAGE_MEMORY = "avgMemory";
    public static final String NAME = "name";
    public static final String PARAMETER = "paramater";
    public static final String TYPE = "type";
    public static final String TOTAL_TIME = "totalTime";
    public static final String TOTAL_MEMORY = "totalMemory";

    @Override
    public MeasurementExport getObject(Node source) throws XMLFormatException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
