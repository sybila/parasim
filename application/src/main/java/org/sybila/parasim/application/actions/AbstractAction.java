/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.application.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.application.ParasimOptions;
import org.sybila.parasim.core.api.Manager;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractAction<R> implements Action<R> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractAction.class);

    private final ParasimOptions options;
    private final Manager manager;

    public AbstractAction(Manager manager, ParasimOptions options) {
        this.options = options;
        this.manager = manager;
    }


    protected ParasimOptions getOptions() {
        return options;
    }

    protected Manager getManager() {
        return manager;
    }

}
