package org.sybila.parasim.visualisation.plot;

import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterRegistrar;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ResultPlotterExtension implements LoadableExtension {

    public void register(ExtensionBuilder builder) {
        builder.extension(ResultPlotterRegistrar.class); //sem přijde konkrétní třída, která se registruje
    }
    
}
