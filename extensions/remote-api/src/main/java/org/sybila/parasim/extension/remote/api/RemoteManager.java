/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
