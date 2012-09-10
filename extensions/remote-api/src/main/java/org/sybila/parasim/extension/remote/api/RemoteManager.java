package org.sybila.parasim.extension.remote.api;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * @author <a href="mailto:xpapous1@mail.muni.cz">Jan Papousek</a>
 */
public interface RemoteManager extends Remote, Serializable {

    <T extends Serializable> Collection<T> service(Class<T> serviceClass) throws RemoteException;

    /**
     * Tries to resolve the instance determined by a pair of the type and qualifier.
     * It uses a root context of the manager.
     *
     * @param <T> type of the wanted instance
     * @param type of the wanted instance
     * @param qualifier of the wanted instance, if {@link org.sybila.parasim.core.annotations.Any} qualifier is used
     * @param context where the wanted instance should be placed, if the the context
     * doesn't contain the instance the parent context is used (if it's available)
     * @return
     */
    <T extends Serializable> T resolve(Class<T> type, Class<? extends Annotation> qualifier) throws RemoteException;

    void forceLoad(Class<? extends Remote> type, Class<? extends Annotation> qualifier) throws RemoteException;

}
