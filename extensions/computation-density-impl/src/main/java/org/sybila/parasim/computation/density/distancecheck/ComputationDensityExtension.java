package org.sybila.parasim.computation.density.distancecheck;

import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ComputationDensityExtension implements LoadableExtension {

    public void register(ExtensionBuilder builder) {
        builder.extension(ComputationDensityRegistrar.class);
    }
}
