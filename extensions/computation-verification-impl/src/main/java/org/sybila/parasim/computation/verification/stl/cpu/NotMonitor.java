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
package org.sybila.parasim.computation.verification.stl.cpu;

import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.cpu.AbstractMonitor;
import org.sybila.parasim.computation.verification.cpu.Monitor;
import org.sybila.parasim.model.verification.Robustness;

/**
 * Monitors the negation of a subformula. The output is a negation of the signal.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class NotMonitor extends AbstractMonitor {

    private final Monitor subMonitor;

    public NotMonitor(Monitor subMonitor) {
        Validate.notNull(subMonitor);
        this.subMonitor = subMonitor;
    }

    public Robustness getRobustness(int index) {
        return subMonitor.getRobustness(index).invert();
    }

    public int size() {
        return subMonitor.size();
    }
}
