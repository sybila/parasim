/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Observes;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.core.event.ManagerStarted;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptor;
import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptorMapper;
import org.sybila.parasim.core.extension.configuration.api.ParasimDescriptor;
import org.sybila.parasim.visualisation.plot.api.PlotterFactory;
import org.sybila.parasim.visualisation.plot.api.event.ResultPlotterRegistered;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ResultPlotterRegistrar {

    @Inject
    private Event<ResultPlotterRegistered> event;
    @Inject
    private Instance<ResultPlotterConfiguration> config;

    public void registerConfiguration(@Observes ManagerStarted event, ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException {
        ResultPlotterConfiguration conf = new ResultPlotterConfiguration();
        ExtensionDescriptor extDesc = descriptor.getExtensionDescriptor("result_plotter");
        if (extDesc != null) {
            mapper.map(extDesc, conf);
        }
        config.set(conf);
    }

    @Provide
    public PlotterFactory register(ResultPlotterConfiguration config) {
        event.fire(new ResultPlotterRegistered());
        return new ProjectionPlotterFactory();
    }
}
