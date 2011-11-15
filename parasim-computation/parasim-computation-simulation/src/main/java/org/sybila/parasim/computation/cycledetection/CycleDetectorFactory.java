package org.sybila.parasim.computation.cycledetection;

/**
 * Enables repeated creation of a specific type of CycleDetector.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
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
