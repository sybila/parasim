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
package org.sybila.parasim.extension.projectmanager.view;

import java.io.File;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.sbml.SBMLOdeSystemFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
@Deprecated
public enum OdeSystemFactory {

    INSTANCE;
    private OdeSystem system = null;

    @SuppressWarnings("UseSpecificCatch")
    private OdeSystemFactory() {
        try {
            system = SBMLOdeSystemFactory.fromFile(new File(getClass().getClassLoader().getResource("SIR.sbml").toURI()));
        } catch (Exception e) {
            System.out.println("Could not load testing ode system.");
            System.exit(1);
        }
    }

    public OdeSystem getTestingOdeSystem() {
        return system;
    }

    public static OdeSystemFactory getInstance() {
        return INSTANCE;
    }
}
