package org.sybila.parasim.core.extension.loader.api;

import org.sybila.parasim.core.LoadableExtension;
import java.util.Collection;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ExtensionLoader {

    Collection<LoadableExtension> load();

}
