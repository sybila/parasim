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
package org.sybila.parasim.model.verification.stl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.sybila.parasim.model.verification.Signal;

/**
 * A simple abstract class representing a general binary operator.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class BinaryFormula extends AbstractFormula {

    private final Formula[] subFormulas;
    private final List<Signal> signals;

    public BinaryFormula(Formula phi1, Formula phi2) {
        super(computeVariableIndexes(phi1, phi2));
        if (phi1 == null) {
            throw new IllegalArgumentException("Parameter phi1 is null.");
        }
        if (phi2 == null) {
            throw new IllegalArgumentException("Parameter phi2 is null.");
        }
        subFormulas = new Formula[]{phi1, phi2};
        Set<Signal> ss = new HashSet<>();
        ss.addAll(phi1.getSignals());
        ss.addAll(phi2.getSignals());
        signals = Collections.unmodifiableList(new ArrayList<>(ss));
    }

    @Override
    public int getArity() {
        return 2;
    }

    @Override
    public Formula getSubformula(int index) {
        if (index < 0 || index > 1) {
            throw new IllegalArgumentException("Index must be 0 or 1.");
        }
        return subFormulas[index];
    }

    @Override
    public List<Signal> getSignals() {
        return signals;
    }

    private static Collection<Integer> computeVariableIndexes(Formula phi1, Formula phi2) {
        Set<Integer> indexes = new HashSet<>();
        indexes.addAll(phi1.getVariableIndexes());
        indexes.addAll(phi2.getVariableIndexes());
        return indexes;
    }
}
