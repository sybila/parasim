package org.sybila.parasim.computation.simulation.api;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public enum Status {

    OK, BOUNDS, PRECISION, TIMEOUT;

    /**
     * Converts an integer to the simulation status
     * @return simulation status
     *
     * @throws IllegalArgumentException if the given integer doesn't correspond
     * to any simulation status
     */
    public static Status fromInt(int status) {
        switch (status) {
            case 0:
                return OK;
            case 1:
                return BOUNDS;
            case 2:
                return PRECISION;
            case 3:
                return TIMEOUT;

            default:
                throw new IllegalStateException("There is no status corresponding to the number [" + status + "].");
        }
    }
}
