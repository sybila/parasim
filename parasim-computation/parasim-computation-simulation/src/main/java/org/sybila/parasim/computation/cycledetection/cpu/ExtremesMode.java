package org.sybila.parasim.computation.cycledetection.cpu;

/**
 * Specifies which extreme values are to be remembered.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public enum ExtremesMode {

    EXTREME_NONE,
    EXTREME_MIN,
    EXTREME_MAX,
    EXTREME_BOTH;

    public static ExtremesMode fromInt(int status) {
    switch(status) {
            case 0:
                return EXTREME_NONE;
            case 1:
        return EXTREME_MIN;
            case 2:
        return EXTREME_MAX;
            case 3:
        return EXTREME_BOTH;
            default:
        throw new IllegalStateException("There is no mode corresponding to the number [" + status + "].");
        }
    }

}