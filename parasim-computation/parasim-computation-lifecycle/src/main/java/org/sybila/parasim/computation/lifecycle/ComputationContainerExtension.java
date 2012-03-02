package org.sybila.parasim.computation.lifecycle;

import org.sybila.parasim.computation.lifecycle.impl.ComputationContainerRegistrar;
import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ComputationContainerExtension implements LoadableExtension {

    public void register(ExtensionBuilder builder) {
        builder.extension(ComputationContainerRegistrar.class);
    }
    
}
