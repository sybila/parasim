package org.sybila.parasim.computation.lifecycle.impl;

import org.sybila.parasim.computation.lifecycle.api.ComputationStatus;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.sybila.parasim.computation.lifecycle.api.annotations.After;
import org.sybila.parasim.computation.lifecycle.api.annotations.Before;
import org.sybila.parasim.computation.lifecycle.api.annotations.Start;
import org.sybila.parasim.computation.lifecycle.api.annotations.Stop;
import org.sybila.parasim.computation.lifecycle.api.annotations.ThreadId;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.ComputationController;
import org.sybila.parasim.core.ContextEvent;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DefaultComputationContainer implements ComputationContainer {

    private Map<Computation, ComputationContext> contexts = new HashMap<Computation, ComputationContext>();
    private ContextEvent<ComputationContext> contextEvent;
    private ServiceFactory serviceFactory;

    public DefaultComputationContainer(ServiceFactory serviceFactory, ContextEvent<ComputationContext> contextEvent) {
        if (serviceFactory == null) {
            throw new IllegalArgumentException("The parameter [serviceFactory] is null.");
        }
        if (contextEvent == null) {
            throw new IllegalArgumentException("The parameter [contextEvent] is null.");
        }
        this.serviceFactory = serviceFactory;
        this.contextEvent = contextEvent;
    }

    @Override
    public void finalize(Computation computation) {
        executeMethods(After.class, computation.getController(), getContext(computation));
        computation.getController().getStatus().setFinalized();
        destroyContext(computation);
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    @Override
    public void init(Computation computation) {
        ComputationContext context = new ComputationContext();
        contextEvent.initialize(context);
        getServiceFactory().provideFieldsAndMethods(computation.getController(), context);
        getServiceFactory().injectFields(computation.getController(), context);
        executeMethods(Before.class, computation.getController(), context);
        computation.getController().getStatus().setInitialized();
    }

    @Override
    public void start(Computation computation) {
        executeMethods(Start.class, computation.getController(), getContext(computation));
    }

    @Override
    public void stop(Computation computation) {
        executeMethods(Stop.class, computation.getController(), getContext(computation));
    }

    private void destroyContext(Computation computation) {
        if (!contexts.containsKey(computation)) {
            return;
        }
        ComputationContext context = contexts.get(computation);
        contextEvent.finalize(context);
        contexts.remove(computation);
    }

    private ComputationContext getContext(Computation computation) {
        if (!contexts.containsKey(computation)) {
            ComputationContext context = new ComputationContext();
            contextEvent.initialize(context);
            contexts.put(computation, context);
        }
        return contexts.get(computation);
    }

    private void executeMethods(Class<? extends Annotation> annotation, final ComputationController controller, ComputationContext context) {
        SortedSet<Method> methods = new TreeSet<Method>(new Comparator<Method>() {

            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        for (Method method : controller.getClass().getDeclaredMethods()) {
            if (method.getAnnotation(annotation) != null) {
                methods.add(method);
            }
        }
        for (final Method method : methods) {
            // non Start annotation
            if (annotation != Start.class) {
                executeMethod(controller, method, context);
                continue;
            }
            // without own thread
            if (!method.getAnnotation(Start.class).ownThread()) {
                if (method.getAnnotation(Start.class).controlsLifeCycle()) {
                    controller.getStatus().startRunning();
                    executeMethod(controller, method, context);
                    controller.getStatus().stopRunning();
                } else {
                    executeMethod(controller, method, context);
                }
                continue;
            }
            // own thread
            int numberOfThreads = method.getAnnotation(Start.class).numberOfThreads() == 0 ? Runtime.getRuntime().availableProcessors() : method.getAnnotation(Start.class).numberOfThreads();
            final ComputationStatus status = controller.getStatus();
            final boolean controlsLifeCycle = method.getAnnotation(Start.class).controlsLifeCycle();
            // start running if it doesn't control life cycle
            if (!controlsLifeCycle) {
                for (int id = 0; id < numberOfThreads; id++) {
                    controller.getStatus().startRunning();
                }
            }
            // construct params
            List<Integer> paramsToInjectThreadId = new ArrayList<Integer>();
            Object[] params = new Object[method.getParameterTypes().length];
            allParams:
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                for (int a = 0; a < method.getParameterAnnotations()[i].length; a++) {
                    if (method.getParameterAnnotations()[i][a] instanceof ThreadId) {
                        paramsToInjectThreadId.add(i);
                        continue allParams;
                    }
                }
                params[i] = getServiceFactory().getService(method.getParameterTypes()[i], context);
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
                            executeMethodWithParams(controller, method, threadParams);
                        } else {
                            executeMethodWithParams(controller, method, threadParams);
                            status.stopRunning();
                        }
                    }
                }).start();
            }
        }
    }

    private void executeMethod(final Object o, Method method, ComputationContext context) {
        Object[] params = new Object[method.getParameterTypes().length];
        for (int i = 0; i < params.length; i++) {
            params[i] = getServiceFactory().getService(method.getParameterTypes()[i], context);
        }
        executeMethodWithParams(o, method, params);
    }

    private void executeMethodWithParams(final Object o, Method method, Object... params) {
        try {
            method.setAccessible(true);
            method.invoke(o, params);
        } catch (Exception e) {
            throw new IllegalStateException("The method can't be executed.", e);
        }
    }
}