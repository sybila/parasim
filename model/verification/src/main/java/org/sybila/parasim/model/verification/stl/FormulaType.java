/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.model.verification.stl;

import java.util.Locale;

import org.sybila.parasim.model.xml.XMLRepresentable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Structural type of a STL formula.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public enum FormulaType implements XMLRepresentable {

    PREDICATE, AND, OR, NOT, UNTIL, FUTURE, GLOBALLY, FREEZE;

    FormulaType fromInt(int value) {
        return values()[value];
    }

    @Override
    public Element toXML(Document doc) {
        return doc.createElement(toString().toLowerCase(Locale.ENGLISH));
    }
}
