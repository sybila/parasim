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
package org.sybila.parasim.extension.projectmanager.view.names;

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface NameList {

    public void addName(String name);

    public void removeName(String name);

    public void renameName(String name, String newName);

    public void selectName(String name);

    public Set<String> getNames();

    public static class Adapter implements NameList {

        @Override
        public void addName(String name) {
        }

        @Override
        public void removeName(String name) {
        }

        @Override
        public void renameName(String name, String newName) {
        }

        @Override
        public void selectName(String name) {
        }

        @Override
        public Set<String> getNames() {
            return Collections.<String>emptySet();
        }
    }
}
