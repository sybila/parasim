package org.sybila.parasim.extension.remote.impl;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.extension.remote.api.RemoteManager;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteManagerImpl extends UnicastRemoteObject implements RemoteManager {

    private final Manager manager;

    public RemoteManagerImpl(Manager manager) throws RemoteException {
        Validate.notNull(manager);
        this.manager = manager;
    }

    @Override
    public <T extends Serializable> Collection<T> service(Class<T> serviceClass) throws RemoteException {
        return manager.service(serviceClass);
    }

    @Override
    public <T extends Serializable> T resolve(Class<T> type, Class<? extends Annotation> qualifier) throws RemoteException {
        return manager.resolve(type, qualifier, manager.getRootContext());
    }

}
