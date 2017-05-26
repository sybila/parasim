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
package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptor;
import org.sybila.parasim.core.api.configuration.ExtensionDescriptorMapper;
import org.sybila.parasim.core.api.configuration.ParasimDescriptor;
import org.sybila.parasim.visualisation.plot.api.PlotterFactory;
import org.sybila.parasim.visualisation.plot.api.annotations.Filling;
import org.sybila.parasim.visualisation.plot.api.annotations.Strict;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ResultPlotterRegistrar {

    @Provide
    public ResultPlotterConfiguration provideConfiguration(ParasimDescriptor descriptor, ExtensionDescriptorMapper mapper) throws IllegalAccessException {
        ResultPlotterConfiguration conf = new ResultPlotterConfiguration();
        ExtensionDescriptor extDesc = descriptor.getExtensionDescriptor("result_plotter");
        if (extDesc != null) {
            mapper.map(extDesc, conf);
        }
        return conf;
    }

    @Strict
    @Provide
    public PlotterFactory registerStrict(ResultPlotterConfiguration config) {
        return new NotFillingProjectionPlotterFactory(config);
    }

    @Filling
    @Provide
    public PlotterFactory registerFilling(ResultPlotterConfiguration config) {
        return new RobustnessFillingProjectionPlotterFactory(config);
    }

    @Provide
    public PlotterFactory registerDefault(ResultPlotterConfiguration config) {
        return new RobustnessFillingProjectionPlotterFactory(config);
    }
}
