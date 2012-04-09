package org.sybila.parasim.core;

import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface LoadableExtension {

    void register(ExtensionBuilder builder);

}
