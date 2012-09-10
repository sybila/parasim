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
package org.sybila.parasim.model.ode;

import java.util.Collection;
import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface OdeSystem extends Iterable<OdeSystemVariable> {

    int dimension();

    Parameter getParameter(int dimension);

    Parameter getParameter(String name);

    OdeSystemVariable getVariable(int dimension);

    OdeSystemVariable getVariable(String name);

    boolean isParamater(int dimension);

    boolean isVariable(int dimension);

    OdeSystem release(Expression... expressions);

    OdeSystem release(Collection<Expression> expressions);

    OdeSystem substitute(ParameterValue... parameterValues);

    OdeSystem substitute(Collection<ParameterValue> parameterValues);

}
