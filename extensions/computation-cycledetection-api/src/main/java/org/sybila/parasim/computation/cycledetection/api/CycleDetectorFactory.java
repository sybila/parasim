package org.sybila.parasim.computation.cycledetection.api;

/**
 * Enables repeated creation of a specific type of CycleDetector.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public interface CycleDetectorFactory<T extends CycleDetector> {

    /**
     * Creates a new cycle detector of a special type and configuration
     * inherited from the factory.
     *
     * @return New CycleDetector.
     */
    public T create();

}
