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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.math.VariableValue;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.model.ode.SimpleOdeSystem;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class AbstractOrthogonalSpaceTest {

    protected static OdeSystem createOdeSystem() {
        String[] names = new String[] {"v", "x", "y", "z"};
        Collection<OdeSystemVariable> vars = new ArrayList<>();
        int index = 0;
        for (String name: names) {
            vars.add(new OdeSystemVariable(name, index, new Constant(0f)));
            index++;
        }
        return new SimpleOdeSystem(vars, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

}
