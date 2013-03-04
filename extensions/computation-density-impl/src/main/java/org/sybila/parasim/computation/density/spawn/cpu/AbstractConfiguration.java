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
package org.sybila.parasim.computation.density.spawn.cpu;

import java.io.Serializable;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractConfiguration implements Configuration, Serializable {

    private final OrthogonalSpace initialSpace;

    public AbstractConfiguration(OrthogonalSpace initialSpace) {
        Validate.notNull(initialSpace);
        this.initialSpace = initialSpace;
    }

    @Override
    public OrthogonalSpace getInitialSpace() {
        return initialSpace;
    }

}
