package org.sybila.parasim.core.extension.loader.api;

import org.sybila.parasim.core.Manager;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ExtensionBuilder {

    void extension(Class<?> extension);

    Manager getManager();

}
