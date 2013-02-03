/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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

/**
 *
 * External resource of objects in XML format. Contains one object of a designated type.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 * @param <T> Type of object being stored.
 */
public interface XMLResource<T extends XMLRepresentable> {

    /**
     * @return Contained object.
     */
    public T getRoot();

    /**
     * Sets new contained object.
     * @param target new contained object.
     */
    public void setRoot(T target);

    /**
     * Stores contained object to XML.
     * @throws XMLException when error during storing occurs.
     */
    public void store() throws XMLException;

    /**
     * Loads an object from XML and sets it as the new contained object.
     * @throws XMLException when error during loading occurs.
     */
    public void load() throws XMLException;
}
