package org.sybila.parasim.extension.exporter.impl;

import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;

public class ResultExporterExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.extension(ResultExporterRegistrar.class);
    }

}
