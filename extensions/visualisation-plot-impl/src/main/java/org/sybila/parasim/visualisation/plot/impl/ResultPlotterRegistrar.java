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
package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.visualisation.plot.api.event.ResultPlotterRegistered;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ResultPlotterRegistrar {

    @Inject
    private Instance<Object> plotter; //sem přijde něco jako ResultPlotter místo Object
    @Inject
    private Event<ResultPlotterRegistered> event;

    public void register(@Observes ManagerStarted startEvent) {
        plotter.set(new Object()); //TODO
        event.fire(new ResultPlotterRegistered());
    }
}
