package org.sybila.parasim.core.extension.configuration.api;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ExtensionDescriptorMapper {

    public void map(ExtensionDescriptor descriptor, Object configBean) throws IllegalAccessException;

}
