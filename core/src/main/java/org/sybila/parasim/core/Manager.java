package org.sybila.parasim.core;

import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Manager {

    void finalizeContext(Context context);

    void fire(Object event, Context context);

    Context getRootContext();

    void initializeContext(Context context);

    void inject(Extension target);

    <T> T resolve(Class<T> type, Context context);

    void shutdown();

    void start();

}
