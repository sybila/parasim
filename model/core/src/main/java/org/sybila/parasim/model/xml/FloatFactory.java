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
package org.sybila.parasim.model.xml;

import org.w3c.dom.Node;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz>Tomáš Vejpuste</a>
 */
public class FloatFactory {

    public static float getObject(Node source) throws XMLFormatException {
        try {
            return Float.valueOf(source.getNodeValue());
        } catch (NumberFormatException nfe) {
            throw new XMLFormatException("Illegible number.", nfe);
        }
    }
}