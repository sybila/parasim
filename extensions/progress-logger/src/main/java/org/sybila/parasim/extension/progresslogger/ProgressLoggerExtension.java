package org.sybila.parasim.extension.progresslogger;

import org.sybila.parasim.core.LoadableExtension;
import org.sybila.parasim.core.extension.loader.api.ExtensionBuilder;
import org.sybila.parasim.core.extension.logging.LoggingListener;
import org.sybila.parasim.extension.progresslogger.impl.ProgressLoggingListener;
import org.sybila.parasim.extension.progresslogger.impl.ProgressLoggerRegistrar;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProgressLoggerExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(LoggingListener.class, ProgressLoggingListener.class);
        builder.extension(ProgressLoggerRegistrar.class);
    }
}
