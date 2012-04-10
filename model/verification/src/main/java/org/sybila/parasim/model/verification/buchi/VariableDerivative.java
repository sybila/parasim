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
package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.ode.OdeSystem;

/**
 * Operand is a given OdeSystem's variable's derivative.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class VariableDerivative<P extends PointDerivative> implements AtomicPropOperand<P> {

    private OdeSystem ode;
    private int varIndex;

    public VariableDerivative(OdeSystem ode, int varIndex) {
        this.ode = ode;
        this.varIndex = varIndex;
    }

    public float getValue(P point) {
        return point.getDerivative(varIndex);
    }

    @Override
    public String toString() {
        return "d" + ode.getVariable(varIndex).getName();
    }
}
