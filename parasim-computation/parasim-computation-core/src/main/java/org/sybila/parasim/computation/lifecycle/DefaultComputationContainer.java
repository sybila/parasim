package org.sybila.parasim.computation.lifecycle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.sybila.parasim.computation.lifecycle.annotations.After;
import org.sybila.parasim.computation.lifecycle.annotations.Before;
import org.sybila.parasim.computation.lifecycle.annotations.Start;
import org.sybila.parasim.computation.lifecycle.annotations.Stop;
import org.sybila.parasim.computation.lifecycle.annotations.ThreadId;
import org.sybila.parasim.model.cdi.ServiceFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DefaultComputationContainer implements ComputationContainer {

    private ServiceFactory serviceFactory;

    public DefaultComputationContainer(ServiceFactory serviceFactory) {
        if (serviceFactory == null) {
            throw new IllegalArgumentException("The parameter [serviceFactory] is null.");
        }
        this.serviceFactory = serviceFactory;
    }

    @Override
    public void finalize(Computation computation) {
        executeMethods(After.class, computation.getController());
        computation.getController().getStatus().setFinalized();
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    @Override
    public void init(Computation computation) {
        getServiceFactory().injectFields(computation.getController());
        executeMethods(Before.class, computation.getController());
        computation.getController().getStatus().setInitialized();
    }

    @Override
    public void start(Computation computation) {
        executeMethods(Start.class, computation.getController());
    }

    @Override
    public void stop(Computation computation) {
        executeMethods(Stop.class, computation.getController());
    }

    private void executeMethods(Class<? extends Annotation> annotation, final ComputationController o) {
        SortedSet<Method> methods = new TreeSet<Method>(new Comparator<Method>() {

            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(annotation) != null) {
                methods.add(method);
            }
        }
        for (final Method method : methods) {
            // non Start annotation
            if (annotation != Start.class) {
                getServiceFactory().executeVoidMethod(o, method);
                continue;
            }
            // without own thread
            if (!method.getAnnotation(Start.class).ownThread()) {
                if (method.getAnnotation(Start.class).controlsLifeCycle()) {
                    o.getStatus().startRunning();
                    getServiceFactory().executeVoidMethod(o, method);
                    o.getStatus().stopRunning();
                } else {
                    getServiceFactory().executeVoidMethod(o, method);
                }
                continue;
            }
            // own thread
            int numberOfThreads = method.getAnnotation(Start.class).numberOfThreads() == 0 ? Runtime.getRuntime().availableProcessors() : method.getAnnotation(Start.class).numberOfThreads();
            final ComputationStatus status = o.getStatus();
            final boolean controlsLifeCycle = method.getAnnotation(Start.class).controlsLifeCycle();
            // start running if it doesn't control life cycle
            if (!controlsLifeCycle) {
                for (int id=0; id<numberOfThreads; id++) {
                    o.getStatus().startRunning();
                }
            }
            // construct params
            List<Integer> paramsToInjectThreadId = new ArrayList<Integer>();
            Object[] params = new Object[method.getParameterTypes().length];
            allParams: for (int i = 0; i < method.getParameterTypes().length; i++) {
                for (int a = 0; a < method.getParameterAnnotations()[i].length; a++) {
                    if (method.getParameterAnnotations()[i][a] instanceof ThreadId) {
                        paramsToInjectThreadId.add(i);
                        continue allParams;
                    }
                }
                params[i] = getServiceFactory().getService(method.getParameterTypes()[i]);
            }
            // start new threads
            for (int id = 0; id < numberOfThreads; id++) {
                final Object[] threadParams;
                if (params.length != 0) {
                    threadParams = Arrays.copyOf(params, params.length);
                    // inject thread ID
                    for (Integer paramToInjectThreadId : paramsToInjectThreadId) {
                        threadParams[paramToInjectThreadId] = id;
                    }

                } else {
                   threadParams = params;
                }
                new Thread(new Runnable() {
                    public void run() {
                        if (controlsLifeCycle) {
                            executeMethod(o, method, threadParams);
                        } else {
                            executeMethod(o, method, threadParams);
                            status.stopRunning();   
                        }
                    }
                }).start();
            }
        }
    }

    private void executeMethod(final Object o, Method method, Object... params) {
        try {
            method.setAccessible(true);
            method.invoke(o, params);
        } catch (Exception e) {
            throw new IllegalStateException("The method can't be executed.", e);
        }
    }
}