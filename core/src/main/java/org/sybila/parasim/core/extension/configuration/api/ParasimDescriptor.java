package org.sybila.parasim.core.extension.configuration.api;

import org.sybila.parasim.core.extension.configuration.api.ExtensionDescriptor;
import java.util.Collection;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ParasimDescriptor {
   
    ExtensionDescriptor getExtensionDescriptor(String name);
    
    Collection<ExtensionDescriptor> getExtensionDescriptors();
    
}
