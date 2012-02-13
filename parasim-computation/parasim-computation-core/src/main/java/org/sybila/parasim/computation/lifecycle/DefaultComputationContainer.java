package org.sybila.parasim.computation.lifecycle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.sybila.parasim.computation.lifecycle.annotations.After;
import org.sybila.parasim.computation.lifecycle.annotations.Before;
import org.sybila.parasim.computation.lifecycle.annotations.Start;
import org.sybila.parasim.computation.lifecycle.annotations.Stop;
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
            ComputationStatus statusToControlLifeCycle = null;
            if (annotation == Start.class && !method.getAnnotation(Start.class).controlsLifeCycle()) {
                statusToControlLifeCycle = o.getStatus();
            }
            final ComputationStatus finalStatusToControlLifeCycle = statusToControlLifeCycle;
            if (annotation == Start.class && method.getAnnotation(Start.class).ownThread()) {
                Runnable ownRunnable = new Runnable() {

                    public void run() {
                        if (finalStatusToControlLifeCycle != null) {
                            finalStatusToControlLifeCycle.startRunning();
                            getServiceFactory().executeVoidMethod(o, method);
                            finalStatusToControlLifeCycle.stopRunning();
                        } else {
                            getServiceFactory().executeVoidMethod(o, method);
                        }
                    }
                };
                new Thread(ownRunnable).start();
            } else {
                if (finalStatusToControlLifeCycle != null) {
                    finalStatusToControlLifeCycle.startRunning();
                    getServiceFactory().executeVoidMethod(o, method);
                    finalStatusToControlLifeCycle.stopRunning();
                } else {
                    getServiceFactory().executeVoidMethod(o, method);
                }
            }
        }
    }
}